package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.HashSet;
import java.util.List;
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
@CustomLog
public class EntityInteractObjective extends Objective {

    private final int notifyInterval;
    private final CompoundLocation loc;
    private final VariableNumber range;
    protected EntityType mobType;
    protected int amount;
    protected String name;
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
        name = instruction.getOptional("name");
        if (name != null) {
            name = name.replace('_', ' ');
            name = ChatColor.translateAlternateColorCodes('&', name);
        }
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
        // if the entity should have a name and it does not match, return
        if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
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
                } catch (final InstructionParseException e) {
                    LOG.reportException(e);
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
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((EntityInteractData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((EntityInteractData) dataMap.get(playerID)).getAmount());
        }
        return "";
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
        public void onRightClick(final PlayerInteractEntityEvent event) {
            final boolean success = onInteract(event.getPlayer(), event.getRightClicked());
            if (success && cancel) {
                event.setCancelled(true);
            }
        }
    }
}
