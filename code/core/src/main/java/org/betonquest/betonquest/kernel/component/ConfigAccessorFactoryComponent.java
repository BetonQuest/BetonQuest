package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConfigAccessorFactory}.
 */
public class ConfigAccessorFactoryComponent extends AbstractCoreComponent {

    /**
     * Create a new ConfigAccessorFactoryComponent.
     */
    public ConfigAccessorFactoryComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultConfigAccessorFactory.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));

        dependencyProvider.take(DefaultConfigAccessorFactory.class, configAccessorFactory);
    }
}
