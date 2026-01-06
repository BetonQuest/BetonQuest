package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 */
public class MobKillObjective extends CountingObjective {

    /**
     * The entity types that should be killed.
     */
    private final Argument<List<EntityType>> entities;

    /**
     * The optional name of the mob.
     */
    @Nullable
    protected Argument<String> name;

    /**
     * The optional marker for the mobs to identify them.
     */
    @Nullable
    protected Argument<String> marked;

    /**
     * Constructor for the MobKillObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the amount of mobs to kill
     * @param entities     the entity types that should be killed
     * @param name         the optional name of the mob
     * @param marked       the optional marker for the mobs to identify them
     * @throws QuestException if there is an error in the instruction
     */
    public MobKillObjective(final Instruction instruction, final Argument<Number> targetAmount,
                            final Argument<List<EntityType>> entities, @Nullable final Argument<String> name,
                            @Nullable final Argument<String> marked) throws QuestException {
        super(instruction, targetAmount, "mobs_to_kill");
        this.entities = entities;
        this.name = name;
        this.marked = marked;
    }

    /**
     * Check if the player has killed the specified mob.
     *
     * @param event   the event containing the mob kill information
     * @param profile the profile of the player that killed the mob
     */
    public void onMobKill(final MobKilledEvent event, final Profile profile) {
        qeHandler.handle(() -> {
            if (!containsPlayer(profile)
                    || !entities.getValue(profile).contains(event.getEntity().getType())
                    || name != null && (event.getEntity().getCustomName() == null
                    || !event.getEntity().getCustomName().equals(name.getValue(profile)))) {
                return;
            }
            if (marked != null) {
                final String value = marked.getValue(profile);
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                final String dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                    return;
                }
            }

            if (checkConditions(profile)) {
                getCountingData(profile).progress();
                completeIfDoneOrNotify(profile);
            }
        });
    }
}
