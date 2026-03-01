package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.kernel.DefaultProcessorDataLoader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} to load the data for all processors.
 */
public class DataLoaderComponent extends AbstractCoreComponent {

    /**
     * Create a new DataLoaderComponent.
     */
    public DataLoaderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, QuestPackageManager.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultProcessorDataLoader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final Reloader reloader = getDependency(Reloader.class);

        final DefaultProcessorDataLoader dataLoader = new DefaultProcessorDataLoader(loggerFactory.create(DefaultProcessorDataLoader.class));

        dependencyProvider.take(DefaultProcessorDataLoader.class, dataLoader);
        reloader.register(ReloadPhase.INSTANCING, () -> dataLoader.loadData(questPackageManager.getPackages().values()));
    }
}
