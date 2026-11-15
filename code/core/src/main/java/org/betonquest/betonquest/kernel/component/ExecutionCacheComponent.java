package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.schedule.LastExecutionCache;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link LastExecutionCache}.
 */
public class ExecutionCacheComponent extends AbstractCoreComponent {

    /**
     * Create a new ExecutionCacheComponent.
     */
    public ExecutionCacheComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(LastExecutionCache.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final LastExecutionCache lastExecutionCache = new LastExecutionCache();

        dependencyProvider.take(LastExecutionCache.class, lastExecutionCache);
    }
}
