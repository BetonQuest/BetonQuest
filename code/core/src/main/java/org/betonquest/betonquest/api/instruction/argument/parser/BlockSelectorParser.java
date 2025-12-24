package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.BlockSelector;

/**
 * Parses a string to a block selector.
 */
public class BlockSelectorParser implements SimpleArgumentParser<BlockSelector> {

    /**
     * Creates a new parser for block selectors.
     */
    public BlockSelectorParser() {
    }

    @Override
    public BlockSelector apply(final String string) throws QuestException {
        return new BlockSelector(string);
    }
}
