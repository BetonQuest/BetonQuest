package org.betonquest.betonquest.quest.placeholder.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;

/**
 * Factory to create {@link SectionPlaceholder}s from {@link Instruction}s.
 */
public class SectionPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * Create a SectionPlaceholder factory.
     */
    public SectionPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullablePlaceholderAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> section = instruction.string().get();
        final Argument<String> identifier = instruction.string().get();
        final Argument<SectionSelectionMode> selectionMode = instruction.enumeration(SectionSelectionMode.class)
                .get("select", SectionSelectionMode.FIRST);
        final Argument<Number> limit = instruction.number().get("limit", Integer.MAX_VALUE);
        final FlagArgument<Boolean> shuffle = instruction.bool().getFlag("shuffle", true);
        final FlagArgument<Boolean> count = instruction.bool().getFlag("count", true);
        return new NullablePlaceholderAdapter(new SectionPlaceholder(instruction.getPackage(), section, identifier, selectionMode, limit, shuffle, count));
    }
}
