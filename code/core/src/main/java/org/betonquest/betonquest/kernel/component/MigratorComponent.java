package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.migrator.Migrator;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.io.IOException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Migrator}.
 */
public class MigratorComponent extends AbstractCoreComponent {

    /**
     * Create a new MigratorComponent.
     */
    public MigratorComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Migrator.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final BetonQuestLogger log = loggerFactory.create(MigratorComponent.class);

        try {
            final Migrator migrator = new Migrator(loggerFactory);
            migrator.migrate();
            dependencyProvider.take(Migrator.class, migrator);
        } catch (final IOException e) {
            log.error("There was an exception while migrating from a previous version! Reason: " + e.getMessage(), e);
        }
    }
}
