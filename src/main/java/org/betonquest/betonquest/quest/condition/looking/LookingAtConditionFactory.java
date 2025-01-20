package org.betonquest.betonquest.quest.condition.looking;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.util.BlockSelector;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link LookingAtCondition}s.
 */
public class LookingAtConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the looking at factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public LookingAtConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.get(instruction.getOptional("loc"), VariableLocation::new);
        final BlockSelector selector = instruction.get(instruction.getOptional("type"), BlockSelector::new);
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        final BetonQuestLogger log = loggerFactory.create(LookingAtCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(createCondition(loc, selector, exactMatch),
                        log, instruction.getPackage()), data
        );
    }

    private LookingAtCondition createCondition(@Nullable final VariableLocation loc, @Nullable final BlockSelector selector, final boolean exactMatch) {
        if (loc != null) {
            return new LookingAtCondition(loc);
        } else if (selector != null) {
            return new LookingAtCondition(selector, exactMatch);
        } else {
            throw new IllegalArgumentException("You must define either 'loc:' or 'type:' optional");
        }
    }
}
