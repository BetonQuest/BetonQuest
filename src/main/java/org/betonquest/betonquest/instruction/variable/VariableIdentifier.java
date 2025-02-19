package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.util.Utils;

/**
 * Adds the quest package to the resolved variable to have a fully qualified identifier.
 */
public class VariableIdentifier extends Variable<String> {

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableIdentifier(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws QuestException {
        super(variableProcessor, pack, input, value -> Utils.addPackage(pack, value));
    }
}
