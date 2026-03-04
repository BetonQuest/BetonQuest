package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.migrator.QuestMigrator;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link QuestManager}.
 */
public class QuestPackageManagerComponent extends AbstractCoreComponent {

    /**
     * Create a new QuestPackageManagerComponent.
     */
    public QuestPackageManagerComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginDescriptionFile.class,
                BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(QuestManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final Reloader reloader = getDependency(Reloader.class);

        final QuestManager questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), configAccessorFactory,
                plugin.getDataFolder(), new QuestMigrator(loggerFactory.create(QuestMigrator.class), descriptionFile));

        dependencyProvider.take(QuestManager.class, questManager);
        reloader.register(ReloadPhase.PACKAGES, questManager::reload);
    }
}
