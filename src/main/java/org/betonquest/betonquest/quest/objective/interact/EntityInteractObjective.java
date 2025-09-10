package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
 * right-click or damage the entity. Each entity can only be interacted once.
 * The interaction can optionally be canceled by adding the argument cancel.
 */
public class EntityInteractObjective extends CountingObjective implements Listener {

    /**
     * The Factory for the Entity Interact Data.
     */
    private static final ObjectiveDataFactory ENTITY_INTERACT_FACTORY = EntityInteractData::new;

    /**
     * The target location of the entity to interact with.
     */
    @Nullable
    private final Variable<Location> loc;

    /**
     * The range around the target location to look for the entity.
     */
    private final Variable<Number> range;

    /**
     * The custom name of the entity to interact with.
     */
    @Nullable
    private final Variable<Component> customName;

    /**
     * The real name of the entity to interact with.
     */
    @Nullable
    private final Variable<String> realName;

    /**
     * The equipment slot to interact with.
     */
    @Nullable
    private final EquipmentSlot slot;

    /**
     * The mob type to interact with.
     */
    protected Variable<EntityType> mobType;

    /**
     * The variable identifier for the marked entities.
     */
    @Nullable
    protected Variable<String> marked;

    /**
     * The interaction type (right, left, any).
     */
    protected Interaction interaction;

    /**
     * Whether to cancel the interaction.
     */
    protected boolean cancel;

    /**
     * Creates a new instance of the EntityInteractObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of entities to interact with
     * @param loc          the location of the entities
     * @param range        the range of the entities
     * @param customName   the custom name of the entities
     * @param realName     the real name of the entities
     * @param slot         the equipment slot to interact with
     * @param mobType      the type of the entities
     * @param marked       the variable identifier for marked entities
     * @param interaction  the interaction type (right, left, any)
     * @param cancel       whether to cancel the interaction
     * @throws QuestException if there is an error in the instruction
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public EntityInteractObjective(final Instruction instruction, final Variable<Number> targetAmount,
                                   @Nullable final Variable<Location> loc,
                                   final Variable<Number> range, @Nullable final Variable<Component> customName,
                                   @Nullable final Variable<String> realName, @Nullable final EquipmentSlot slot,
                                   final Variable<EntityType> mobType, @Nullable final Variable<String> marked,
                                   final Variable<Interaction> interaction, final boolean cancel) throws QuestException {
        super(instruction, ENTITY_INTERACT_FACTORY, targetAmount, "mobs_to_click");
        this.loc = loc;
        this.range = range;
        this.customName = customName;
        this.realName = realName;
        this.slot = slot;
        this.mobType = mobType;
        this.marked = marked;
        this.interaction = interaction.getValue(null);
        this.cancel = cancel;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private boolean onInteract(final Player player, final Entity entity) throws QuestException {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile)) {
            return false;
        }
        if (!entity.getType().equals(mobType.getValue(onlineProfile))) {
            return false;
        }
        if (customName != null && (entity.customName() == null || !customName.getValue(onlineProfile).equals(entity.customName()))) {
            return false;
        }
        if (realName != null && !realName.getValue(onlineProfile).equals(entity.getName())) {
            return false;
        }
        // check if the entity is correctly marked
        if (marked != null) {
            final String value = marked.getValue(onlineProfile);
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
            final String dataContainerValue = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                return false;
            }
        }
        // check if the profile has this objective
        if (!checkConditions(onlineProfile)) {
            return false;
        }
        // Check location matches
        if (loc != null) {
            final Location location = loc.getValue(onlineProfile);
            final double pRange = range.getValue(onlineProfile).doubleValue();
            if (!entity.getWorld().equals(location.getWorld())
                    || entity.getLocation().distanceSquared(location) > pRange * pRange) {
                return false;
            }
        }

        final boolean success = Objects.requireNonNull((EntityInteractData) dataMap.get(onlineProfile)).tryProgressWithEntity(entity);
        if (success) {
            completeIfDoneOrNotify(onlineProfile);
        }
        return success;
    }

    /**
     * The left click event handler.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (interaction == Interaction.RIGHT) {
            return;
        }
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
        qeHandler.handle(() -> {
            final boolean success = onInteract(player, event.getEntity());
            if (success && cancel) {
                event.setCancelled(true);
            }
        });
    }

    /**
     * The right click event handler.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true)
    public void onRightClick(final PlayerInteractEntityEvent event) {
        if (interaction == Interaction.LEFT || slot != null && slot != event.getHand()) {
            return;
        }
        qeHandler.handle(() -> {
            final boolean success = onInteract(event.getPlayer(), event.getRightClicked());
            if (success && cancel) {
                event.setCancelled(true);
            }
        });
    }

    /**
     * The right click event handler specific for armor stands.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true)
    public void onArmorRightClick(final PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            onRightClick(event);
        }
    }

    /**
     * The entity counting data for the objective.
     */
    public static class EntityInteractData extends CountingData {
        /**
         * The set of entities that have been interacted with.
         */
        private final Set<UUID> entities;

        /**
         * Creates a new instance of the EntityInteractData.
         *
         * @param instruction the instruction that created this objective
         * @param profile     the profile of the player
         * @param objID       the ID of the objective
         * @throws QuestException when the instruction data is malformed
         */
        public EntityInteractData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
            super(instruction, profile, objID);
            entities = new HashSet<>();
            final String[] entityInstruction = instruction.split(";", 3);
            if (entityInstruction.length >= 2 && !entityInstruction[1].isEmpty()) {
                Arrays.stream(entityInstruction[1].split("/"))
                        .map(UUID::fromString)
                        .forEach(entities::add);
            }
        }

        /**
         * Checks if the interaction with a given entity progresses the objective.
         *
         * @param entity the entity to try to progress with
         * @return true if the entity was added to the set, false otherwise
         */
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
}
