package org.betonquest.betonquest.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * This {@link UpdateSourceHandler} handles all {@link ReleaseUpdateSource} and {@link DevelopmentUpdateSource}
 * instances and searches for updates in them.
 * When calling {@link #searchUpdateFor(Pair, List, VersionComparator, UpdateSourceConsumer)},
 * it will provide the latest version and the URL to download it from.
 */
public class UpdateSourceHandler {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * A list of {@link ReleaseUpdateSource} instances.
     */
    private final List<ReleaseUpdateSource> releaseHandlerList;

    /**
     * A list of {@link DevelopmentUpdateSource} instances.
     */
    private final List<DevelopmentUpdateSource> developmentHandlerList;

    /**
     * Creates a new {@link UpdateSourceHandler} with the given lists.
     * One list for {@link ReleaseUpdateSource} and one for {@link DevelopmentUpdateSource}
     * can be passed. One is for release builds, the other for development builds.
     *
     * @param log                    the logger that will be used for logging
     * @param releaseHandlerList     A list of {@link ReleaseUpdateSource} instances
     * @param developmentHandlerList A list of {@link DevelopmentUpdateSource} instances
     */
    public UpdateSourceHandler(final BetonQuestLogger log, final List<ReleaseUpdateSource> releaseHandlerList, final List<DevelopmentUpdateSource> developmentHandlerList) {
        this.log = log;
        this.releaseHandlerList = releaseHandlerList;
        this.developmentHandlerList = developmentHandlerList;
    }

    /**
     * Searches for updates in the provided {@link ReleaseUpdateSource} and {@link DevelopmentUpdateSource} lists
     * and returns the latest version with the URL to download it from.
     * If there is no update available, the URL in the pair is null.
     * <p>
     * If an update is searched, it will then first search in the list of {@link ReleaseUpdateSource} instances
     * and then in the list of {@link DevelopmentUpdateSource} instances.
     * Development builds are only searched, if the {@link UpdaterConfig} is configured for it.
     *
     * @param config       The {@link UpdaterConfig} containing all settings
     * @param current      The current {@link Version}
     * @param devIndicator The version qualifier for a dev build
     * @return a par of the latest version and the corresponding download url
     */
    @VisibleForTesting
    Pair<Version, String> searchUpdate(final UpdaterConfig config, final Version current, final String devIndicator) {
        final VersionComparator comparator = new VersionComparator(config.getStrategy(), devIndicator + "-");
        Pair<Version, String> latest = Pair.of(current, null);
        latest = searchUpdateFor(latest, releaseHandlerList, comparator, releaseUpdateSource -> releaseUpdateSource.getReleaseVersions(current));
        if (config.isDevDownloadEnabled() && !(latest.getValue() != null && config.isForcedStrategy())) {
            latest = searchUpdateFor(latest, developmentHandlerList, comparator, developmentUpdateSource -> developmentUpdateSource.getDevelopmentVersions(current));
        }
        return latest;
    }

    private <T> Pair<Version, String>
    searchUpdateFor(final Pair<Version, String> latest, final List<T> updateSources, final VersionComparator comparator,
                    final UpdateSourceConsumer<T> consumer) {
        Pair<Version, String> currentLatest = latest;
        for (final T updateSource : updateSources) {
            try {
                for (final Map.Entry<Version, String> entry : consumer.consume(updateSource).entrySet()) {
                    if (comparator.isOtherNewerThanCurrent(latest.getKey(), entry.getKey())) {
                        currentLatest = Pair.of(entry.getKey(), entry.getValue());
                    }
                }
            } catch (final UnknownHostException e) {
                log.warn("Could not fetch version updates. Probably the host is currently not available: " + e.getMessage());
            } catch (final IOException e) {
                log.warn("Could not fetch version updates from a source: " + e.getMessage(), e);
            }
        }
        return currentLatest;
    }

    /**
     * Interface for a function that consumes an update source of type {@link T}
     * and returns a map of versions and download urls.
     * This is used to provide a generic way to consume both
     * {@link ReleaseUpdateSource} and {@link DevelopmentUpdateSource}.
     *
     * @param <T> The type of the update source.
     */
    private interface UpdateSourceConsumer<T> {

        /**
         * Applies this function to the given argument.
         *
         * @param updateSource the function argument
         * @return the function result
         * @throws IOException when there is something wrong during this operation.
         */
        Map<Version, String> consume(T updateSource) throws IOException;
    }
}
