package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.regex.PatternSyntaxException;

/**
 * Matches the content against a regex.
 */
public class VariableCondition implements NullableCondition {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The content to match against the regex.
     */
    private final Argument<String> content;

    /**
     * The regex the content must match.
     */
    private final Argument<String> regex;

    /**
     * The address of the id for logging.
     */
    private final String instructionId;

    /**
     * Whether to force synchronization with the main server thread.
     */
    private final boolean forceSync;

    /**
     * Creates a new VariableCondition based on the given instruction.
     *
     * @param log           the logger
     * @param content       the content to match against the regex
     * @param regex         the regex the content must match
     * @param instructionId the address of the id for logging
     * @param forceSync     whether to force synchronization with the main server thread
     */
    public VariableCondition(final BetonQuestLogger log, final Argument<String> content, final Argument<String> regex,
                             final String instructionId, final boolean forceSync) {
        this.log = log;
        this.content = content;
        this.regex = regex;
        this.instructionId = instructionId;
        this.forceSync = forceSync;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final String content = this.content.getValue(profile);
        final String regex = this.regex.getValue(profile);
        try {
            return content.matches(regex);
        } catch (final PatternSyntaxException e) {
            log.warn("Invalid regular expression '%s' used in variable condition '%s'. Error: %s"
                    .formatted(e.getPattern(), instructionId, e.getMessage()), e);
            return false;
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return forceSync;
    }
}
