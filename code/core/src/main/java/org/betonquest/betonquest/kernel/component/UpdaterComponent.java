package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.lib.version.BetonQuestVersion;
import org.betonquest.betonquest.web.DownloadSource;
import org.betonquest.betonquest.web.TempFileDownloadSource;
import org.betonquest.betonquest.web.WebContentSource;
import org.betonquest.betonquest.web.WebDownloadSource;
import org.betonquest.betonquest.web.updater.UpdateDownloader;
import org.betonquest.betonquest.web.updater.UpdateSourceHandler;
import org.betonquest.betonquest.web.updater.Updater;
import org.betonquest.betonquest.web.updater.UpdaterConfig;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.web.updater.source.implementations.GitHubReleaseSource;
import org.betonquest.betonquest.web.updater.source.implementations.ReposiliteReleaseAndDevelopmentSource;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.InstantSource;
import java.util.List;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Updater}.
 */
public class UpdaterComponent extends AbstractCoreComponent {

    /**
     * The indicator for dev versions.
     */
    public static final String DEV_INDICATOR = "DEV";

    /**
     * The URL to the betonquest reposilite server.
     */
    public static final String REPOSILITE_URL = "https://repo.betonquest.org";

    /**
     * The name of the betonquest repository.
     */
    public static final String REPOSITORY_NAME = "betonquest";

    /**
     * The ID used for the reposilite pom mapper.
     */
    public static final String POM_MAPPER_ID = "BetonQuest";

    /**
     * The URL to reposilite pom mapper endpoint.
     */
    public static final String REPO_API_URL = "https://api.github.com/repos/BetonQuest/BetonQuest";

    /**
     * Creates a new UpdaterComponent.
     */
    public UpdaterComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class, PluginDescriptionFile.class,
                Server.class, ConfigAccessor.class, BetonQuestLoggerFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Updater.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final Server server = getDependency(Server.class);
        final BukkitScheduler scheduler = getDependency(BukkitScheduler.class);
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Reloader reloader = getDependency(Reloader.class);

        final DownloadSource downloadSource = new TempFileDownloadSource(new WebDownloadSource());
        final UpdateDownloader updateDownloader = new UpdateDownloader(downloadSource, server.getUpdateFolderFile(), plugin.getDescription());
        final ReposiliteReleaseAndDevelopmentSource reposiliteReleaseAndDevelopmentSource =
                new ReposiliteReleaseAndDevelopmentSource(REPOSILITE_URL, REPOSITORY_NAME, POM_MAPPER_ID, new WebContentSource());
        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource(REPO_API_URL, new WebContentSource(GitHubReleaseSource.HTTP_CODE_HANDLER));
        final List<ReleaseUpdateSource> releaseHandlers = List.of(reposiliteReleaseAndDevelopmentSource, gitHubReleaseSource);
        final List<DevelopmentUpdateSource> developmentHandlers = List.of(reposiliteReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(loggerFactory.create(UpdateSourceHandler.class),
                releaseHandlers, developmentHandlers);

        final Version pluginVersion = BetonQuestVersion.parse(descriptionFile.getVersion());
        final UpdaterConfig updaterConfig = new UpdaterConfig(loggerFactory.create(UpdaterConfig.class), config, pluginVersion, DEV_INDICATOR);
        final Updater updater = new Updater(loggerFactory.create(Updater.class), updaterConfig, pluginVersion, updateSourceHandler, updateDownloader,
                plugin, scheduler, InstantSource.system());

        dependencyProvider.take(Updater.class, updater);
        reloader.register(ReloadPhase.PACKAGES, updater::search);
    }
}
