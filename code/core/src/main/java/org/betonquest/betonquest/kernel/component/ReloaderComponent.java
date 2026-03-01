package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
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
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultReloader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        dependencyProvider.take(DefaultReloader.class, new DefaultReloader(loggerFactory.create(DefaultReloader.class)));
    }
}
