package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link PlaceholderVariable}s from {@link Instruction}s.
 */
public class PlaceholderVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * The empty default constructor.
     */
    public PlaceholderVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullableVariableAdapter parseInstruction(final Instruction instruction) {
        final String placeholder = String.join(".", instruction.getValueParts());
        return new NullableVariableAdapter(new PlaceholderVariable(placeholder));
    }
}
