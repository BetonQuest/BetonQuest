package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.lib.versioning.java.JREVersionPrinter;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for version information.
 */
public class VersionInfoComponent extends AbstractCoreComponent {

    /**
     * Create a new VersionInfoComponent.
     */
    public VersionInfoComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(PluginDescriptionFile.class, BetonQuestLoggerFactory.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final BetonQuestLogger log = loggerFactory.create(VersionInfoComponent.class);

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        final String version = descriptionFile.getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.info(jreInfo);
    }
}
