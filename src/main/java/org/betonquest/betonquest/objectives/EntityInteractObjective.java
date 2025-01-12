package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
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
     * The key for any hand.
     */
    private static final String ANY = "any";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    @Nullable
    private final VariableLocation loc;

    private final VariableNumber range;

    @Nullable
    private final String customName;

    @Nullable
    private final String realName;

    @Nullable
    private final EquipmentSlot slot;

    protected EntityType mobType;

    @Nullable
    protected VariableString marked;

    protected Interaction interaction;

    protected boolean cancel;

    @Nullable
    private RightClickListener rightClickListener;

    @Nullable
    private LeftClickListener leftClickListener;

    public EntityInteractObjective(final Instruction instruction) throws QuestException {
        super(instruction, "mobs_to_click");
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        template = EntityInteractData.class;
        interaction = instruction.getEnum(Interaction.class);
        mobType = instruction.getEnum(EntityType.class);
        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        customName = parseName(instruction.getOptional("name"));
        realName = parseName(instruction.getOptional("realname"));
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        cancel = instruction.hasArgument("cancel");
        loc = instruction.getLocation(instruction.getOptional("loc"));
        final String stringRange = instruction.getOptional("range");
        range = instruction.getVarNum(stringRange == null ? "1" : stringRange);
        final String handString = instruction.getOptional("hand");
        if (handString == null || handString.equalsIgnoreCase(EquipmentSlot.HAND.toString())) {
            slot = EquipmentSlot.HAND;
        } else if (handString.equalsIgnoreCase(EquipmentSlot.OFF_HAND.toString())) {
            slot = EquipmentSlot.OFF_HAND;
        } else if (ANY.equalsIgnoreCase(handString)) {
            slot = null;
        } else {
            throw new QuestException("Invalid hand value: " + handString);
        }
    }

    @Nullable
    private String parseName(@Nullable final String rawName) {
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
            final String value = marked.getString(PlayerConverter.getID(player));
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
            final String dataContainerValue = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                return false;
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
                final Location location = loc.getValue(onlineProfile);
                final double pRange = range.getValue(onlineProfile).doubleValue();
                if (!entity.getWorld().equals(location.getWorld())
                        || entity.getLocation().distance(location) > pRange) {
                    return false;
                }
            } catch (final QuestException e) {
                log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
            }
        }

        final boolean success = Objects.requireNonNull((EntityInteractData) dataMap.get(onlineProfile)).tryProgressWithEntity(entity);
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
            if (slot != null && slot != EquipmentSlot.HAND) {
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
            if (slot != null && slot != event.getHand()) {
                return;
            }
            final boolean success = onInteract(event.getPlayer(), event.getRightClicked());
            if (success && cancel) {
                event.setCancelled(true);
            }
        }
    }
}
