package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.integration.IntegrationManager;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Compatibility}.
 */
public class CompatibilityComponent extends AbstractCoreComponent {

    /**
     * Create a new CompatibilityComponent.
     */
    public CompatibilityComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, BetonQuestApi.class, IntegrationManager.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Compatibility.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final BetonQuestApi betonQuestApi = getDependency(BetonQuestApi.class);
        final IntegrationManager integrationManager = getDependency(IntegrationManager.class);

        final Compatibility compatibility = new Compatibility(loggerFactory.create(Compatibility.class), betonQuestApi, integrationManager);

        dependencyProvider.take(Compatibility.class, compatibility);
    }
}
