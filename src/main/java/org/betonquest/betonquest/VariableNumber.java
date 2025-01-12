package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Represents a number that can contain variables.
 *
 * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableNumber} instead
 */
@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
@Deprecated
public class VariableNumber extends org.betonquest.betonquest.instruction.variable.VariableNumber {
    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param questPackage the package in which the variable is used in
     * @param input        the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableNumber#VariableNumber(
     *org.betonquest.betonquest.quest.registry.processor.VariableProcessor, QuestPackage, String)} instead
     */
    @Deprecated
    public VariableNumber(final QuestPackage questPackage, final String input) throws QuestException {
        super(BetonQuest.getInstance().getVariableProcessor(), questPackage, input, (value) -> {
        });
    }
}
