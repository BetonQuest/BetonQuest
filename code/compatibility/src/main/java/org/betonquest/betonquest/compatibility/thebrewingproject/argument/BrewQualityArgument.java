package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.brew.BrewQuality;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A collection of arguments representing a brew quality condition.
 *
 * @param qualityArgument   A brew quality
 * @param operationArgument A comparison operator
 */
public record BrewQualityArgument(Argument<BrewQuality> qualityArgument,
                                  Argument<Operation> operationArgument) implements Argument<Predicate<BrewQuality>> {

    /**
     * Parse an instruction chain.
     *
     * @param instruction The instruction chain to parse
     * @return A new {@link BrewQualityArgument}
     * @throws QuestException If the argument could not be parsed
     */
    public static BrewQualityArgument parseInstructions(final Instruction instruction) throws QuestException {
        final Argument<Operation> operationArgument = instruction.parse(Operation::fromSymbol).get();
        final Argument<BrewQuality> qualityArgument = instruction.enumeration(BrewQuality.class).get();
        return new BrewQualityArgument(qualityArgument, operationArgument);
    }

    private int value(final BrewQuality quality) {
        return switch (quality) {
            case BAD -> 0;
            case GOOD -> 1;
            case EXCELLENT -> 2;
        };
    }

    @Override
    public Predicate<BrewQuality> getValue(@Nullable final Profile profile) throws QuestException {
        final BrewQuality quality = qualityArgument.getValue(profile);
        final Operation operation = operationArgument.getValue(profile);
        return other -> operation.check(value(quality), value(other));
    }
}
