package org.betonquest.betonquest.quest.action.test;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;

/**
 * The factory to create {@link ConditionTestAction}s from {@link Instruction}s.
 */
public class ConditionTestActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The default message to log on test failure.
     */
    public static final String DEFAULT_MESSAGE_ON_FAILURE = "Test '%s' on condition '%s' failed. Expected '%s', but was '%s'";

    /**
     * The default message to log when the test succeeds.
     */
    public static final String DEFAULT_MESSAGE_ON_SUCCESS = "Test '%s' on condition '%s' succeeded.";

    /**
     * The value of the independent flag if it set but no explicit value is defined.
     */
    public static final boolean DEFAULT_FLAGGED_TEST_INDEPENDENTLY = true;

    /**
     * If the optional expected value is not set, this is the default value.
     */
    public static final boolean DEFAULT_EXPECTED_VALUE = true;

    /**
     * The logger factory to create a logger for the {@link ConditionTestAction}.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The condition manager to evaluate conditions.
     */
    private final ConditionManager conditionManager;

    /**
     * Sole constructor.
     *
     * @param loggerFactory    the logger factory to create a logger for the {@link ConditionTestAction}
     * @param conditionManager the condition manager to evaluate conditions
     */
    public ConditionTestActionFactory(final BetonQuestLoggerFactory loggerFactory, final ConditionManager conditionManager) {
        this.loggerFactory = loggerFactory;
        this.conditionManager = conditionManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    private NullableActionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<ConditionIdentifier> conditionIdentifier = instruction.identifier(ConditionIdentifier.class).get();
        final Argument<Boolean> expectedValue = instruction.bool().get("expected", DEFAULT_EXPECTED_VALUE);
        final FlagArgument<Boolean> testIndependently = instruction.bool().getFlag("independent", DEFAULT_FLAGGED_TEST_INDEPENDENTLY);
        final Argument<String> failureMessage = instruction.string().get("fail", DEFAULT_MESSAGE_ON_FAILURE);
        final Argument<String> successMessage = instruction.string().get("success", DEFAULT_MESSAGE_ON_SUCCESS);
        final ConditionTestAction testConditionAction = new ConditionTestAction(loggerFactory.create(ConditionTestAction.class), instruction.getID(), conditionManager,
                conditionIdentifier, expectedValue, testIndependently, failureMessage, successMessage);
        return new NullableActionAdapter(testConditionAction);
    }
}
