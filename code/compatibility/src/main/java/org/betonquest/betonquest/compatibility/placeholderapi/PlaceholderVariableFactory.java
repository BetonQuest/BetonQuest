package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;

/**
 * Factory to create {@link PlaceholderVariable}s from {@link DefaultInstruction}s.
 */
public class PlaceholderVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * The empty default constructor.
     */
    public PlaceholderVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final DefaultInstruction instruction) {
        return parseInstruction(instruction);
    }

    private NullableVariableAdapter parseInstruction(final DefaultInstruction instruction) {
        final String placeholder = String.join(".", instruction.getValueParts());
        return new NullableVariableAdapter(new PlaceholderVariable(placeholder));
    }
}
