package org.betonquest.betonquest.quest.variable;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.VariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.QuestTypeAdapter;

/**
 * Factory adapter for that will provide both {@link PlayerVariable} and {@link PlayerlessVariable} implementations
 * from the supplied {@link VariableFactory}.
 */
public class VariableFactoryAdapter extends QuestTypeAdapter<Variable, PlayerVariable, PlayerlessVariable> implements PlayerVariableFactory, PlayerlessVariableFactory {
    /**
     * Create a new VariableFactoryAdapter to create {@link PlayerVariable}s and {@link PlayerlessVariable}s from it.
     *
     * @param variableFactory the factory used to parse the instruction.
     */
    public VariableFactoryAdapter(final QuestFactory<Variable> variableFactory) {
        super(variableFactory);
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }
}
