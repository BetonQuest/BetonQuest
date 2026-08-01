package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.TypeFactory;

import java.util.function.Function;

/**
 * Factory to create {@link PointCondition}s for a given {@link PointHolder}.
 */
public class AbstractPointConditionFactory implements TypeFactory<PointCondition> {

    /**
     * Function to get the point holder from a profile.
     */
    private final Function<Profile, PointHolder> holderFunction;

    /**
     * Create a new factory for {@link PointHolder}.
     *
     * @param holderFunction the function to get the point holder from a profile
     */
    public AbstractPointConditionFactory(final Function<Profile, PointHolder> holderFunction) {
        this.holderFunction = holderFunction;
    }

    @Override
    public PointCondition parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        final Argument<Number> count = instruction.number().get();
        final FlagArgument<Boolean> equal = instruction.bool().getFlag("equal", true);
        final FlagArgument<Number> fallback = instruction.number().getFlag("fallback", 0);
        return new PointCondition(holderFunction, category, count, equal, fallback);
    }
}
