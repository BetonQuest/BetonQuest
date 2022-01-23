package pl.betoncraft.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Player has to interact with specified amount of specified mobs. It can also
 * require the player to interact with specifically named mobs and notify them
 * about the required amount. It can be specified if the player has to
 * rightclick or damage the entity. Each entity can only be interacted once.
 * The interaction can optionally be canceled by adding the argument cancel.
 */
@SuppressWarnings("PMD.CommentRequired")
public class EntityInteractObjective extends Objective {

    private final int notifyInterval;
    private final CompoundLocation loc;
    private final VariableNumber range;
    private final String customName;
    private final String realName;
    protected EntityType mobType;
    protected int amount;
    protected String marked;
    protected boolean notify;
    protected Interaction interaction;
    protected boolean cancel;
    private RightClickListener rightClickListener;
    private LeftClickListener leftClickListener;

    public EntityInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = EntityInteractData.class;
        interaction = instruction.getEnum(Interaction.class);
        mobType = instruction.getEnum(EntityType.class);
        amount = instruction.getPositive();
        customName = parseName(instruction.getOptional("name"));
        realName = parseName(instruction.getOptional("realname"));
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
        cancel = instruction.hasArgument("cancel");
        loc = instruction.getLocation(instruction.getOptional("loc"));
        final String stringRange = instruction.getOptional("range");
        range = instruction.getVarNum(stringRange == null ? "1" : stringRange);
    }

    private String parseName(final String rawName) {
        if (rawName != null) {
            return ChatColor.translateAlternateColorCodes('&', rawName.replace('_', ' '));
        }
        return null;
    }

    @Override
    public void start() {
        switch (interaction) {
            case RIGHT:
                rightClickListener = new RightClickListener();
                break;
            case LEFT:
                leftClickListener = new LeftClickListener();
                break;
            case ANY:
                rightClickListener = new RightClickListener();
                leftClickListener = new LeftClickListener();
                break;
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private boolean onInteract(final Player player, final Entity entity) {
        // check if it's the right entity type
        if (!entity.getType().equals(mobType)) {
            return false;
        }
        if (customName != null && (entity.getCustomName() == null || !entity.getCustomName().equals(customName))) {
            return false;
        }
        if (realName != null && !realName.equals(entity.getName())) {
            return false;
        }
        // check if the entity is correctly marked
        if (marked != null) {
            if (!entity.hasMetadata("betonquest-marked")) {
                return false;
            }
            final List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
            for (final MetadataValue m : meta) {
                if (!m.asString().equals(marked)) {
                    return false;
                }
            }
        }
        // check if the player has this objective
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return false;
        }
        // Check location matches
        if (loc != null) {
            try {
                final Location location = loc.getLocation(playerID);
                final double pRange = range.getDouble(playerID);
                if (!entity.getWorld().equals(location.getWorld())
                        || entity.getLocation().distance(location) > pRange) {
                    return false;
                }
            } catch (final QuestRuntimeException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
        }


        // get data off the player
        final EntityInteractData playerData = (EntityInteractData) dataMap.get(playerID);
        // check if player already interacted with entity
        if (playerData.containsEntity(entity)) {
            return false;
        }
        // right mob is interacted with, handle data update
        playerData.subtract();
        playerData.addEntity(entity);
        if (playerData.isZero()) {
            completeObjective(playerID);
        } else if (notify && playerData.getAmount() % notifyInterval == 0) {
            // send a notification
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "mobs_to_click", new String[]{String.valueOf(playerData.getAmount())},
                        "mobs_to_click,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'mobs_to_click' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException exep) {
                    LogUtils.logThrowableReport(exep);
                }
            }
        }
        return true;
    }

    @Override
    public void stop() {
        if (rightClickListener != null) {
            HandlerList.unregisterAll(rightClickListener);
        }
        if (leftClickListener != null) {
            HandlerList.unregisterAll(leftClickListener);
        }
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "amount":
                return Integer.toString(amount - ((EntityInteractData) dataMap.get(playerID)).getAmount());
            case "left":
                return Integer.toString(((EntityInteractData) dataMap.get(playerID)).getAmount());
            case "total":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    public enum Interaction {
        RIGHT, LEFT, ANY
    }

    public static class EntityInteractData extends ObjectiveData {

        private final Set<UUID> entitys;
        private int amount;

        public EntityInteractData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            final String[] args = instruction.split(" ");
            amount = Integer.parseInt(args[0].trim());
            entitys = new HashSet<>();
            for (int i = 1; i < args.length; i++) {
                entitys.add(UUID.fromString(args[i]));
            }
        }

        public void addEntity(final Entity entity) {
            entitys.add(entity.getUniqueId());
            update();
        }

        public boolean containsEntity(final Entity entity) {
            return entitys.contains(entity.getUniqueId());
        }

        public int getAmount() {
            return amount;
        }

        public void subtract() {
            amount--;
            update();
        }

        public boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            final StringBuilder string = new StringBuilder(Integer.toString(amount));
            for (final UUID uuid : entitys) {
                string.append(" ").append(uuid.toString());
            }
            return string.toString();
        }

    }

    private class LeftClickListener implements Listener {
        public LeftClickListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamage(final EntityDamageByEntityEvent event) {
            final Player player;
            // check if entity is damaged by a Player
            if (event.getDamager() instanceof Player) {
                player = (Player) event.getDamager();
            } else {
                return;
            }
            final boolean succes = onInteract(player, event.getEntity());
            if (succes && cancel) {
                event.setCancelled(true);
            }
        }
    }

    private class RightClickListener implements Listener {
        public RightClickListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler(ignoreCancelled = true)
        public void onRightClick(final PlayerInteractAtEntityEvent event) {
            final boolean success = onInteract(event.getPlayer(), event.getRightClicked());
            if (success && cancel) {
                event.setCancelled(true);
            }
        }
    }
}
