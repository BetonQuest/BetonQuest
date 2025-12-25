package org.betonquest.betonquest.quest.condition.looking;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link LookingAtCondition}s.
 */
public class LookingAtConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the looking at factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public LookingAtConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final Argument<BlockSelector> selector = instruction.blockSelector()
                .get("type").orElse(null);
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        final BetonQuestLogger log = loggerFactory.create(LookingAtCondition.class);
        return new OnlineConditionAdapter(createCondition(loc, selector, exactMatch), log, instruction.getPackage());
    }

    private LookingAtCondition createCondition(@Nullable final Argument<Location> loc, @Nullable final Argument<BlockSelector> selector, final boolean exactMatch) {
        if (loc != null) {
            return new LookingAtCondition(loc);
        }
        if (selector != null) {
            return new LookingAtCondition(selector, exactMatch);
        }
        throw new IllegalArgumentException("You must define either 'loc:' or 'type:' optional");
    }
}
