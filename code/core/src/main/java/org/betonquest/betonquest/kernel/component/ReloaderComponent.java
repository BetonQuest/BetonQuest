package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.kernel.DefaultReloader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultReloader}.
 */
public class ReloaderComponent extends AbstractCoreComponent {

    /**
     * Create a new ReloaderComponent.
     */
    public ReloaderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of();
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultReloader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        dependencyProvider.take(DefaultReloader.class, new DefaultReloader());
    }
}
