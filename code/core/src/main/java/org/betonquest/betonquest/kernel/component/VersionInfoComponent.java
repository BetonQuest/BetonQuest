package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.lib.version.JREVersionReader;
import org.betonquest.betonquest.meta.MetaDataAcceptor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Map;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for version information.
 */
public class VersionInfoComponent extends AbstractCoreComponent {

    /**
     * The metadata key for the BetonQuest version.
     */
    private static final String META_BETONQUEST_VERSION = "bq_version";

    /**
     * The metadata key for the Java version.
     */
    private static final String META_JAVA_VERSION = "java_version";

    /**
     * Create a new VersionInfoComponent.
     */
    public VersionInfoComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(PluginDescriptionFile.class, BetonQuestLoggerFactory.class, MetaDataAcceptor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final MetaDataAcceptor metaDataAcceptor = getDependency(MetaDataAcceptor.class);

        final BetonQuestLogger log = loggerFactory.create(VersionInfoComponent.class);

        final JREVersionReader jreVersionReader = new JREVersionReader();
        final String version = descriptionFile.getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.info(jreVersionReader.getReadableVersionInformation());

        final Map<String, String> jreVersion = jreVersionReader.getVersionInformation();
        metaDataAcceptor.acceptChange(META_JAVA_VERSION, section -> jreVersion.forEach(section::set),
                entry -> !jreVersion.equals(entry.getValue().getValues(false)));
        metaDataAcceptor.acceptChange(META_BETONQUEST_VERSION, section -> section.set("version", version),
                entry -> !version.equals(entry.getValue().getString("version")));
    }
}
