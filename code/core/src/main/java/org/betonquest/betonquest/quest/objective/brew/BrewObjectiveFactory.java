package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link BrewObjective} instances from {@link Instruction}s.
 */
public class BrewObjectiveFactory implements ObjectiveFactory {

    /**
     * Profile provider to get the profile of the player.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new instance of the BrewObjectiveFactory.
     *
     * @param profileProvider the profile provider to get the profile of the player
     */
    public BrewObjectiveFactory(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<QuestItemWrapper> potion = instruction.item().get();
        final Variable<Number> targetAmount = instruction.number().atLeast(0).get();
        return new BrewObjective(instruction, targetAmount, profileProvider, potion);
    }
}
