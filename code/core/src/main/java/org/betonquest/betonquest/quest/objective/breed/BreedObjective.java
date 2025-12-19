package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

/**
 * Requires the player to breed a specific type of animal.
 */
public class BreedObjective extends CountingObjective implements Listener {
    /**
     * The type of animal to breed.
     */
    private final Variable<EntityType> type;

    /**
     * Constructor for the BreedObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of animals to breed
     * @param type         the type of animal to breed
     * @throws QuestException if there is an error in the instruction
     */
    public BreedObjective(final Instruction instruction, final Variable<Number> targetAmount, final Variable<EntityType> type) throws QuestException {
        super(instruction, targetAmount, "animals_to_breed");
        this.type = type;
    }

    /**
     * Check if the player is breeding the right type of animal.
     *
     * @param event the event that triggered the breeding
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        qeHandler.handle(() -> {
            if (event.getBreeder() instanceof Player) {
                final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getBreeder());
                if (event.getEntityType() == type.getValue(onlineProfile) && containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                    getCountingData(onlineProfile).progress();
                    completeIfDoneOrNotify(onlineProfile);
                }
            }
        });
    }
}
