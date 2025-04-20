package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.util.BlockSelector;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a block selector that can contain variables.
 */
public class VariableBlockSelector extends Variable<BlockSelector> {
    /**
     * Resolves a string that may contain variables to a BlockSelector.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableBlockSelector(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input) throws QuestException {
        super(variableProcessor, pack, input, BlockSelector::new);
    }
}
