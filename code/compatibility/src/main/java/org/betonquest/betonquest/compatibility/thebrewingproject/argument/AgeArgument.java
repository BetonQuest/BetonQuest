package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewingStep;
import dev.jsinco.brewery.api.breweries.BarrelType;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of arguments representing an aging step argument.
 *
 * @param barrelTypeArgument Barrel type argument
 * @param agingYearsArgument Aging years argument
 * @param ageingYear         How long an aging year is for TheBrewingProject (configurable)
 */
public record AgeArgument(Argument<BarrelType> barrelTypeArgument,
                          Argument<Number> agingYearsArgument,
                          long ageingYear) implements Argument<QuestFunction<Brew, Boolean>> {

    /**
     * Create an age argument from an instruction chain.
     *
     * @param instruction An instruction chain
     * @param ageingYear  An aging years as defined by TheBrewingProject
     * @return A new age argument based on the instruction
     * @throws QuestException If any of the arguments is invalid
     */
    public static AgeArgument fromChain(final Instruction instruction, final long ageingYear) throws QuestException {
        final Argument<BarrelType> barrelTypeArgument = instruction.parse(new BreweryKeyedParser<>(BreweryRegistry.BARREL_TYPE)).get();
        final Argument<Number> ageTimeArgument = instruction.number().atLeast(0.5).get();
        return new AgeArgument(barrelTypeArgument, ageTimeArgument, ageingYear);
    }

    @Override
    public QuestFunction<Brew, Boolean> getValue(@Nullable final Profile profile) throws QuestException {
        final BarrelType barrelType = barrelTypeArgument.getValue(profile);
        final double ageTime = agingYearsArgument.getValue(profile).doubleValue();
        return brew -> brew.lastCompletedStep() instanceof final BrewingStep.Age age
                && barrelType.proximityScore(age.barrelType()) == 1
                && ageTime > (double) age.time().moment() / ageingYear;
    }
}
