package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Represents a string that can contain variables.
 *
 * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString} instead
 */
@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
@Deprecated
public class VariableString extends org.betonquest.betonquest.instruction.variable.VariableString {
    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param questPackage the package in which the variable is used in
     * @param input        the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString#VariableString(
     *org.betonquest.betonquest.quest.registry.processor.VariableProcessor, QuestPackage, String)} instead
     */
    @Deprecated
    public VariableString(final QuestPackage questPackage, final String input) throws QuestException {
        super(BetonQuest.getInstance().getVariableProcessor(), questPackage, input);
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param questPackage       the package in which the variable is used in
     * @param input              the string that may contain variables
     * @param replaceUnderscores whether underscores should be replaced
     * @throws QuestException if the variables could not be created or resolved to the given type
     * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString#VariableString(
     *VariableProcessor, QuestPackage, String, boolean)} instead
     */
    @Deprecated
    public VariableString(final QuestPackage questPackage, final String input, final boolean replaceUnderscores)
            throws QuestException {
        super(BetonQuest.getInstance().getVariableProcessor(), questPackage, input, replaceUnderscores);
    }
}
