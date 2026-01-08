package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityBreedEvent;

/**
 * Requires the player to breed a specific type of animal.
 */
public class BreedObjective extends CountingObjective {

    /**
     * The type of animal to breed.
     */
    private final Argument<EntityType> type;

    /**
     * Constructor for the BreedObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the target amount of animals to breed
     * @param type         the type of animal to breed
     * @throws QuestException if there is an error in the instruction
     */
    public BreedObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount, final Argument<EntityType> type) throws QuestException {
        super(service, targetAmount, "animals_to_breed");
        this.type = type;
    }

    /**
     * Check if the player is breeding the right type of animal.
     *
     * @param event         the event that triggered the breeding
     * @param onlineProfile the profile of the player that breeds the animal
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onBreeding(final EntityBreedEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getEntityType() == type.getValue(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }
}
