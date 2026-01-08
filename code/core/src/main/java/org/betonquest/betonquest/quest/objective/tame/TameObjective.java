package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
     * @param service      the objective factory service
     * @param targetAmount the target amount of entities to be tamed
     * @param type         the entity type to be tamed
     * @throws QuestException if there is an error in the instruction
     */
    public TameObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                         final Argument<EntityType> type) throws QuestException {
        super(service, targetAmount, "animals_to_tame");
        this.type = type;
    }

    /**
     * Handles the taming event.
     *
     * @param event         the taming event
     * @param onlineProfile the profile of the player that tamed the entity
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onTaming(final EntityTameEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (containsPlayer(onlineProfile) && type.getValue(onlineProfile) == event.getEntity().getType() && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }
}
