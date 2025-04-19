package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final Item potion = instruction.getItem();
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ZERO);
        return new BrewObjective(instruction, targetAmount, profileProvider, potion);
    }
}
