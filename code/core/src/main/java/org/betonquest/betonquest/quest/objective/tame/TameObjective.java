package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * The player must tame specified amount of specified mobs.
 */
public class TameObjective extends CountingObjective {

    /**
     * The entity type to be tamed.
     */
    private final Argument<EntityType> type;

    /**
     * Constructor for the TameObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of entities to be tamed
     * @param type         the entity type to be tamed
     * @throws QuestException if there is an error in the instruction
     */
    public TameObjective(final Instruction instruction, final Argument<Number> targetAmount,
                         final Argument<EntityType> type) throws QuestException {
        super(instruction, targetAmount, "animals_to_tame");
        this.type = type;
    }

    /**
     * Handles the taming event.
     *
     * @param event         the taming event
     * @param onlineProfile the profile of the player that tamed the entity
     */
    public void onTaming(final EntityTameEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            if (containsPlayer(onlineProfile) && type.getValue(onlineProfile) == event.getEntity().getType() && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        });
    }
}
