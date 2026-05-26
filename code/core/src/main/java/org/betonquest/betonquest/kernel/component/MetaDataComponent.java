package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.meta.MetaDataHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link MetaDataHandler}.
 */
public class MetaDataComponent extends AbstractCoreComponent {

    /**
     * The name of the folder where the metadata is stored.
     */
    private static final String METADATA_FOLDER_NAME = ".meta";

    /**
     * Create a new MetaDataComponent.
     */
    public MetaDataComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(MetaDataHandler.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);

        final File metaFolder = new File(plugin.getDataFolder(), METADATA_FOLDER_NAME);
        if (!metaFolder.exists() && !metaFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create metadata folder at '%s'.".formatted(metaFolder.getAbsolutePath()));
        }

        final MetaDataHandler metaDataHandler = new MetaDataHandler(loggerFactory.create(MetaDataHandler.class), metaFolder, configAccessorFactory);
        dependencyProvider.take(MetaDataHandler.class, metaDataHandler);
    }
}
