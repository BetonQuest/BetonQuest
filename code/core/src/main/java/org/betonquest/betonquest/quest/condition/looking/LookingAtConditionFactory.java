package org.betonquest.betonquest.quest.condition.looking;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link LookingAtCondition}s.
 */
public class LookingAtConditionFactory implements PlayerConditionFactory {

    /**
     * Create the looking at factory.
     */
    public LookingAtConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final Argument<BlockSelector> selector = instruction.blockSelector()
                .get("type").orElse(null);
        final FlagArgument<Boolean> exactMatch = instruction.bool().getFlag("exactMatch", true);
        return new OnlineConditionAdapter(createCondition(loc, selector, exactMatch));
    }

    private LookingAtCondition createCondition(@Nullable final Argument<Location> loc, @Nullable final Argument<BlockSelector> selector,
                                               final FlagArgument<Boolean> exactMatch) throws QuestException {
        if (loc != null) {
            return new LookingAtCondition(loc);
        }
        if (selector != null) {
            return new LookingAtCondition(selector, exactMatch);
        }
        throw new QuestException("You must define either 'loc:' or 'type:' optional");
    }
}
