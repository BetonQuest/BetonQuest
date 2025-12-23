package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.regex.PatternSyntaxException;

/**
 * Checks if the variable value matches given pattern.
 */
public class VariableCondition implements NullableCondition {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The variable to compare with the regex.
     */
    private final Variable<String> variable;

    /**
     * The regex the variable must match.
     */
    private final Variable<String> regex;

    /**
     * The address of the variable for logging.
     */
    private final String variableAddress;

    /**
     * Whether to force synchronization with the main server thread.
     */
    private final boolean forceSync;

    /**
     * Creates a new VariableCondition based on the given instruction.
     *
     * @param log             the logger
     * @param variable        the variable to compare with the regex
     * @param regex           the regex the variable must match
     * @param variableAddress the address of the variable for logging
     * @param forceSync       whether to force synchronization with the main server thread
     */
    public VariableCondition(final BetonQuestLogger log, final Variable<String> variable, final Variable<String> regex,
                             final String variableAddress, final boolean forceSync) {
        this.log = log;
        this.variable = variable;
        this.regex = regex;
        this.variableAddress = variableAddress;
        this.forceSync = forceSync;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final String resolvedVariable = variable.getValue(profile);
        final String resolvedRegex = regex.getValue(profile);
        try {
            return resolvedVariable.matches(resolvedRegex);
        } catch (final PatternSyntaxException e) {
            log.warn("Invalid regular expression '%s' used in variable condition '%s'. Error: %s"
                    .formatted(e.getPattern(), variableAddress, e.getMessage()), e);
            return false;
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return forceSync;
    }
}
