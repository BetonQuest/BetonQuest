package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player has to interact with specified amount of specified mobs. It can also
 * require the player to interact with specifically named mobs and notify them
 * about the required amount. It can be specified if the player has to
 * rightclick or damage the entity. Each entity can only be interacted once.
 * The interaction can optionally be canceled by adding the argument cancel.
 */
@SuppressWarnings("PMD.CommentRequired")
public class EntityInteractObjective extends CountingObjective {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create();

    private final CompoundLocation loc;

    private final VariableNumber range;

    private final String customName;

    private final String realName;

    protected EntityType mobType;

    protected String marked;

    protected Interaction interaction;

    protected boolean cancel;

    private RightClickListener rightClickListener;

    private LeftClickListener leftClickListener;

    public EntityInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_to_click");
        template = EntityInteractData.class;
        interaction = instruction.getEnum(Interaction.class);
        mobType = instruction.getEnum(EntityType.class);
        targetAmount = instruction.getPositive();
        customName = parseName(instruction.getOptional("name"));
        realName = parseName(instruction.getOptional("realname"));
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
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

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
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
                if (!m.asString().equals(marked.replace("%player%", PlayerConverter.getID(player).getProfileUUID().toString()))) {
                    return false;
                }
            }
        }
        // check if the profile has this objective
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return false;
        }
        // Check location matches
        if (loc != null) {
            try {
                final Location location = loc.getLocation(onlineProfile);
                final double pRange = range.getDouble(onlineProfile);
                if (!entity.getWorld().equals(location.getWorld())
                        || entity.getLocation().distance(location) > pRange) {
                    return false;
                }
            } catch (final QuestRuntimeException e) {
                LOG.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
            }
        }

        final boolean success = ((EntityInteractData) dataMap.get(onlineProfile)).tryProgressWithEntity(entity);
        if (success) {
            completeIfDoneOrNotify(onlineProfile);
        }
        return success;
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

    public enum Interaction {
        RIGHT, LEFT, ANY
    }

    public static class EntityInteractData extends CountingData {

        private final Set<UUID> entities;

        @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
        public EntityInteractData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
            entities = new HashSet<>();
            final String[] entityInstruction = instruction.split(";", 3);
            if (entityInstruction.length >= 2 && !entityInstruction[1].isEmpty()) {
                Arrays.stream(entityInstruction[1].split("/"))
                        .map(UUID::fromString)
                        .forEach(entities::add);
            }
        }

        public boolean tryProgressWithEntity(final Entity entity) {
            final boolean success = entities.add(entity.getUniqueId());
            if (success) {
                progress();
            }
            return success;
        }

        @Override
        public String toString() {
            return super.toString() + ";" + entities.stream().map(UUID::toString).collect(Collectors.joining("/"));
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
            final boolean success = onInteract(player, event.getEntity());
            if (success && cancel) {
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
