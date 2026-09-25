package org.betonquest.betonquest.quest.action.test;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

/**
 * The test action allows for unit-test-like validation of conditions in the betonquest script itself and
 * therefore allows for more robust testing outside the codebase.
 * <br> <br>
 * It offers to define an expected value as well as messages for logging purposes on fail.
 */
public class ConditionTestAction implements NullableAction {

    /**
     * The message to log when a test is skipped due to a missing profile.
     *
     * @see #testIndependently
     */
    public static final String MESSAGE_TEST_SKIPPED = "Test '%s' skipped due to profile missing.";

    /**
     * If the {@link #testIndependently} flag is not set, this is the default value.
     */
    public static final boolean DEFAULT_TEST_INDEPENDENTLY = false;

    /**
     * The condition identifier of the condition to validate.
     * <br> <br>
     */
    private final Argument<ConditionIdentifier> conditionToValidate;

    /**
     * The expected value of the condition to compare against.
     * <br> <br>
     * A mismatch is considered a failed test.
     */
    private final Argument<Boolean> expectedValue;

    /**
     * If this argument is true, the condition will be tested independently of the profile if there is one.
     * <br> <br>
     * Important to notice: this simultaneously narrows down the room for error.
     * While this is true, testing with a profile will ignore the profile.
     * However, if this is set to false, testing without a profile will automatically skip the test and log that failed
     * attempt.
     */
    private final FlagArgument<Boolean> testIndependently;

    /**
     * The message to log on test failure.
     * <br> <br>
     * May contain placeholders replaced by {@link String#formatted(Object...)}.
     * <ul>
     *     <li>%1$s - the identifier of the test action itself</li>
     *     <li>%2$s - the identifier of the condition to validate</li>
     *     <li>%3$s - the expected value of the condition</li>
     *     <li>%4$s - the actual value of the condition</li>
     * </ul>
     * If not defined, use the default message as defined in {@link ConditionTestActionFactory#DEFAULT_MESSAGE_ON_FAILURE}.
     */
    private final Argument<String> messageOnFailure;

    /**
     * The message to log on test success.
     * <br> <br>
     * May contain placeholders replaced by {@link String#formatted(Object...)}.
     * <ul>
     *     <li>%1$s - the identifier of the test action itself</li>
     *     <li>%2$s - the identifier of the condition to validate</li>
     * </ul>
     * If not defined, use the default message as defined in {@link ConditionTestActionFactory#DEFAULT_MESSAGE_ON_SUCCESS}.
     */
    private final Argument<String> messageOnSuccess;

    /**
     * The identifier of the test action itself for logging purposes.
     */
    private final Identifier testIdentifier;

    /**
     * The logger to use for logging test results.
     */
    private final BetonQuestLogger logger;

    /**
     * The condition manager to validate conditions.
     */
    private final ConditionManager conditionManager;

    /**
     * Creates a new test action.
     *
     * @param logger              the logger to use for logging test results
     * @param testIdentifier      the identifier of the test action itself for logging purposes
     * @param conditionManager    the condition manager to validate conditions
     * @param conditionToValidate the condition to validate
     * @param expectedValue       the expected value of the condition
     * @param testIndependently   whether to test the condition independently of the profile
     * @param messageOnFailure    the message to log on test failure
     * @param messageOnSuccess    the message to log on test success
     */
    public ConditionTestAction(final BetonQuestLogger logger, final Identifier testIdentifier, final ConditionManager conditionManager,
                               final Argument<ConditionIdentifier> conditionToValidate, final Argument<Boolean> expectedValue,
                               final FlagArgument<Boolean> testIndependently, final Argument<String> messageOnFailure,
                               final Argument<String> messageOnSuccess) {
        this.logger = logger;
        this.testIdentifier = testIdentifier;
        this.conditionManager = conditionManager;
        this.conditionToValidate = conditionToValidate;
        this.expectedValue = expectedValue;
        this.testIndependently = testIndependently;
        this.messageOnFailure = messageOnFailure;
        this.messageOnSuccess = messageOnSuccess;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final boolean independent = testIndependently.getValue(profile).orElse(DEFAULT_TEST_INDEPENDENTLY);
        if (!independent && profile == null) {
            logger.error(MESSAGE_TEST_SKIPPED.formatted(testIdentifier));
            return;
        }
        final Profile profileToUse = independent ? null : profile;
        final ConditionIdentifier conditionIdentifier = conditionToValidate.getValue(profileToUse);
        final boolean expected = expectedValue.getValue(profileToUse);
        final boolean actual = conditionManager.test(profileToUse, conditionIdentifier);
        if (actual == expected) {
            final String successMessage = messageOnSuccess.getValue(profileToUse);
            logger.info(successMessage.formatted(testIdentifier, conditionIdentifier));
            return;
        }
        final String failureMessage = messageOnFailure.getValue(profileToUse);
        logger.error(failureMessage.formatted(testIdentifier, conditionIdentifier, expected, actual));
    }
}
