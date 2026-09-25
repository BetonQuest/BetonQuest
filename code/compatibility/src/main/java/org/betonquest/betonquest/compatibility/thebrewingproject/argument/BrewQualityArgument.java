package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.brew.BrewQuality;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.quest.condition.number.Operation;

import java.util.function.Predicate;

/**
 * A collection of arguments representing a brew quality condition.
 *
 * @param qualityArgument   a brew quality
 * @param operationArgument a comparison operator
 */
public record BrewQualityArgument(Argument<BrewQuality> qualityArgument,
                                  Argument<Operation> operationArgument) {

    /**
     * Parse an instruction chain. Takes an {@link Operation}, and then a {@link BrewQuality}
     * argument.
     *
     * @param instruction the instruction chain to parse
     * @return a new {@link BrewQualityArgument}
     * @throws QuestException if the argument could not be parsed
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

    /**
     * Resolve these arguments based on a profile.
     *
     * @param profile the profile to resolve the arguments against
     * @return a predicate for testing brew quality
     * @throws QuestException if any argument is invalid
     */
    public Predicate<BrewQuality> resolve(final Profile profile) throws QuestException {
        final BrewQuality quality = qualityArgument.getValue(profile);
        final Operation operation = operationArgument.getValue(profile);
        return other -> operation.check(value(other), value(quality));
    }
}
