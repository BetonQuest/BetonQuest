package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholderAdapter;

/**
 * Factory to create {@link PlaceholderAPIPlaceholder}s from {@link Instruction}s.
 */
public class PlaceholderAPIPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * The empty default constructor.
     */
    public PlaceholderAPIPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) {
        return parseInstruction(instruction);
    }

    private NullablePlaceholderAdapter parseInstruction(final Instruction instruction) {
        final String placeholder = String.join(".", instruction.getValueParts());
        return new NullablePlaceholderAdapter(new PlaceholderAPIPlaceholder(placeholder));
    }
}
