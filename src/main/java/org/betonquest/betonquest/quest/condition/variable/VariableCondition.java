package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
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
    private final VariableString variable;

    /**
     * The regex the variable must match.
     */
    private final VariableString regex;

    /**
     * The address of the variable for logging.
     */
    private final String variableAddress;

    /**
     * Creates a new VariableCondition based on the given instruction.
     *
     * @param log             the logger
     * @param variable        the variable to compare with the regex
     * @param regex           the regex the variable must match
     * @param variableAddress the address of the variable for logging
     */
    public VariableCondition(final BetonQuestLogger log, final VariableString variable, final VariableString regex, final String variableAddress) {
        this.log = log;
        this.variable = variable;
        this.regex = regex;
        this.variableAddress = variableAddress;
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
}
