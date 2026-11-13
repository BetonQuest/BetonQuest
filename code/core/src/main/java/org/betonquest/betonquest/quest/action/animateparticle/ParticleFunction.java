package org.betonquest.betonquest.quest.action.animateparticle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a particle function.
 */
public class ParticleFunction {

    /**
     * The function to calculate the particle's x position.
     */
    private final Argument<MathFunction> functionX;

    /**
     * The function to calculate the particle's y position.
     */
    private final Argument<MathFunction> functionY;

    /**
     * The function to calculate the particle's z position.
     */
    private final Argument<MathFunction> functionZ;

    /**
     * The function provider for functions to be calculated.
     */
    private final FunctionProvider provider;

    /**
     * Creates a new particle function.
     *
     * @param functionX function to calculate x position
     * @param functionY function to calculate y position
     * @param functionZ function to calculate z position
     * @param provider  function provider
     */
    public ParticleFunction(final Argument<MathFunction> functionX, final Argument<MathFunction> functionY,
                            final Argument<MathFunction> functionZ, final FunctionProvider provider) {
        this.functionX = functionX;
        this.functionY = functionY;
        this.functionZ = functionZ;
        this.provider = provider;
    }

    /**
     * Calculates the particle's position based on the given function.
     *
     * @param profile the profile to calculate the position for
     * @param start   the start value of the function
     * @param stop    the stop value of the function
     * @param steps   the steps of the function
     * @return the calculated position
     * @throws QuestException if the function could not be evaluated
     */
    public List<Vector> calculateSeries(@Nullable final Profile profile, final double start, final double stop, final int steps) throws QuestException {
        final MathFunction xFunction = functionX.getValue(profile);
        final MathFunction yFunction = functionY.getValue(profile);
        final MathFunction zFunction = functionZ.getValue(profile);
        final List<Vector> series = new ArrayList<>(steps);
        final double step = (stop - start) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            final double delta = start + i * step;
            final FunctionAssignment x = xFunction.evaluate(provider, List.of(new NumberSourceAssignment(delta)));
            final FunctionAssignment y = yFunction.evaluate(provider, List.of(new NumberSourceAssignment(delta)));
            final FunctionAssignment z = zFunction.evaluate(provider, List.of(new NumberSourceAssignment(delta)));
            series.add(new Vector(x.asNumber().doubleValue(), y.asNumber().doubleValue(), z.asNumber().doubleValue()));
        }
        return series;
    }
}
