package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewingStep;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * A Collection of arguments representing a distill argument.
 *
 * @param distillRunsArgument The argument for the amount of runs to distill
 */
public record DistillArgument(Argument<Number> distillRunsArgument) implements Argument<QuestFunction<Brew, Boolean>> {

    /**
     * Parse the instruction chain.
     *
     * @param instruction The instruction chain
     * @return A new distill argument
     * @throws QuestException If any argument was invalid
     */
    public static DistillArgument fromChain(final Instruction instruction) throws QuestException {
        final Argument<Number> distillRunsArgument = instruction.number().atLeast(1).get();
        return new DistillArgument(distillRunsArgument);
    }

    @Override
    public QuestFunction<Brew, Boolean> getValue(@Nullable final Profile profile) throws QuestException {
        final int distillRuns = distillRunsArgument.getValue(profile).intValue();
        return brew -> brew.lastCompletedStep() instanceof final BrewingStep.Distill distill
                && distill.runs() == distillRuns;
    }
}
