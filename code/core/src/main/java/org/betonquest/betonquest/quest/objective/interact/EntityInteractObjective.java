package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Player has to interact with specified amount of specified mobs. It can also
 * require the player to interact with specifically named mobs and notify them
 * about the required amount. It can be specified if the player has to
 * right-click or damage the entity. Each entity can only be interacted once.
 * The interaction can optionally be canceled by adding the argument cancel.
 */
public class EntityInteractObjective extends CountingObjective {

    /**
     * The target location of the entity to interact with.
     */
    @Nullable
    private final Argument<Location> loc;

    /**
     * The range around the target location to look for the entity.
     */
    private final Argument<Number> range;

    /**
     * The custom name of the entity to interact with.
     */
    @Nullable
    private final Argument<Component> customName;

    /**
     * The real name of the entity to interact with.
     */
    @Nullable
    private final Argument<String> realName;

    /**
     * The equipment slot to interact with.
     */
    @Nullable
    private final EquipmentSlot slot;

    /**
     * The mob type to interact with.
     */
    protected Argument<EntityType> mobType;

    /**
     * The identifier for the marked entities.
     */
    @Nullable
    protected Argument<String> marked;

    /**
     * The interaction type (right, left, any).
     */
    protected Interaction interaction;

    /**
     * Whether to cancel the interaction.
     */
    protected FlagArgument<Boolean> cancel;

    /**
     * Creates a new instance of the EntityInteractObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the target amount of entities to interact with
     * @param loc          the location of the entities
     * @param range        the range of the entities
     * @param customName   the custom name of the entities
     * @param realName     the real name of the entities
     * @param slot         the equipment slot to interact with
     * @param mobType      the type of the entities
     * @param marked       the identifier for marked entities
     * @param interaction  the interaction type (right, left, any)
     * @param cancel       whether to cancel the interaction
     * @throws QuestException if there is an error in the instruction
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public EntityInteractObjective(final ObjectiveService service, final Argument<Number> targetAmount,
                                   @Nullable final Argument<Location> loc,
                                   final Argument<Number> range, @Nullable final Argument<Component> customName,
                                   @Nullable final Argument<String> realName, @Nullable final EquipmentSlot slot,
                                   final Argument<EntityType> mobType, @Nullable final Argument<String> marked,
                                   final Argument<Interaction> interaction, final FlagArgument<Boolean> cancel) throws QuestException {
        super(service, targetAmount, "mobs_to_click");
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
    private boolean onInteract(final OnlineProfile onlineProfile, final Entity entity) throws QuestException {
        if (entity.getType() != mobType.getValue(onlineProfile)) {
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
        // Check location matches
        if (loc != null) {
            final Location location = loc.getValue(onlineProfile);
            final double pRange = range.getValue(onlineProfile).doubleValue();
            if (!entity.getWorld().equals(location.getWorld())
                    || entity.getLocation().distanceSquared(location) > pRange * pRange) {
                return false;
            }
        }

        final String data = getService().getData().get(onlineProfile);
        final EntityInteractData interactData = new EntityInteractData(data, onlineProfile, getObjectiveID());
        final boolean success = interactData.tryProgressWithEntity(entity);
        if (success) {
            completeIfDoneOrNotify(onlineProfile);
        }
        return success;
    }

    /**
     * The left click event handler.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that interacted with the entity
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onDamage(final EntityDamageByEntityEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (interaction == Interaction.RIGHT) {
            return;
        }
        if (slot != null && slot != EquipmentSlot.HAND) {
            return;
        }

        final boolean success = onInteract(onlineProfile, event.getEntity());
        if (success && cancel.getValue(onlineProfile).orElse(false)) {
            event.setCancelled(true);
        }
    }

    /**
     * The right click event handler.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that interacted with the entity
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onRightClick(final PlayerInteractEntityEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (interaction == Interaction.LEFT || slot != null && slot != event.getHand()) {
            return;
        }
        final boolean success = onInteract(onlineProfile, event.getRightClicked());
        if (success && cancel.getValue(onlineProfile).orElse(false)) {
            event.setCancelled(true);
        }
    }

    /**
     * The right click event handler specific for armor stands.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that interacted with the entity
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onArmorRightClick(final PlayerInteractAtEntityEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getRightClicked() instanceof ArmorStand) {
            onRightClick(event, onlineProfile);
        }
    }
}
