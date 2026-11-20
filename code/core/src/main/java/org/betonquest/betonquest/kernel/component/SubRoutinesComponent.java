package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.function.subroutine.AbsSubRoutine;
import org.betonquest.betonquest.function.subroutine.AvgSubRoutine;
import org.betonquest.betonquest.function.subroutine.CeilSubRoutine;
import org.betonquest.betonquest.function.subroutine.ClampSubRoutine;
import org.betonquest.betonquest.function.subroutine.CosSubRoutine;
import org.betonquest.betonquest.function.subroutine.ExpSubRoutine;
import org.betonquest.betonquest.function.subroutine.FloorSubRoutine;
import org.betonquest.betonquest.function.subroutine.LogSubRoutine;
import org.betonquest.betonquest.function.subroutine.MaxSubRoutine;
import org.betonquest.betonquest.function.subroutine.MinSubRoutine;
import org.betonquest.betonquest.function.subroutine.PiSubRoutine;
import org.betonquest.betonquest.function.subroutine.RndSubRoutine;
import org.betonquest.betonquest.function.subroutine.RoundSubRoutine;
import org.betonquest.betonquest.function.subroutine.SinSubRoutine;
import org.betonquest.betonquest.function.subroutine.SqrtSubRoutine;
import org.betonquest.betonquest.function.subroutine.SumSubRoutine;
import org.betonquest.betonquest.function.subroutine.TanSubRoutine;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for subroutines registered with {@link Functions}.
 */
public class SubRoutinesComponent extends AbstractCoreComponent {

    /**
     * Create a new SubRoutinesComponent.
     */
    public SubRoutinesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Functions.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Functions functions = getDependency(Functions.class);
        functions.registerSubRoutine("abs", new AbsSubRoutine());
        functions.registerSubRoutine("avg", new AvgSubRoutine());
        functions.registerSubRoutine("ceil", new CeilSubRoutine());
        functions.registerSubRoutine("clamp", new ClampSubRoutine());
        functions.registerSubRoutine("cos", new CosSubRoutine());
        functions.registerSubRoutine("exp", new ExpSubRoutine());
        functions.registerSubRoutine("floor", new FloorSubRoutine());
        functions.registerSubRoutine("log", new LogSubRoutine());
        functions.registerSubRoutine("max", new MaxSubRoutine());
        functions.registerSubRoutine("min", new MinSubRoutine());
        functions.registerSubRoutine("pi", new PiSubRoutine());
        functions.registerSubRoutine("rnd", new RndSubRoutine());
        functions.registerSubRoutine("round", new RoundSubRoutine());
        functions.registerSubRoutine("sin", new SinSubRoutine());
        functions.registerSubRoutine("sqrt", new SqrtSubRoutine());
        functions.registerSubRoutine("sum", new SumSubRoutine());
        functions.registerSubRoutine("tan", new TanSubRoutine());
    }
}
