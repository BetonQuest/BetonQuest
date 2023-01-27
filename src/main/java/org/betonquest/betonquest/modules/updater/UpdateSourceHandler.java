package org.betonquest.betonquest.modules.updater;

import lombok.CustomLog;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.modules.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.modules.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.modules.updater.source.UpdateSource;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Two lists of {@link UpdateSource}s can be passed to this class in the constructor.
 * One is for release builds, the other for development builds.
 * <p>
 * If an update is searched, it will then first search in the list of {@link ReleaseUpdateSource} instances
 * and then in the list of {@link DevelopmentUpdateSource} instances.
 * Development builds are only searched, if the {@link UpdaterConfig} is configured for it.
 */
@CustomLog
public class UpdateSourceHandler {

    /**
     * A list of {@link ReleaseUpdateSource} instances.
     */
    private final List<ReleaseUpdateSource> releaseHandlerList;
    /**
     * A list of {@link DevelopmentUpdateSource} instances.
     */
    private final List<DevelopmentUpdateSource> developmentHandlerList;

    /**
     * Creates a new {@link UpdateSourceHandler} with the given {@link UpdateSource} lists.
     *
     * @param releaseHandlerList     A list of {@link ReleaseUpdateSource} instances
     * @param developmentHandlerList A list of {@link DevelopmentUpdateSource} instances
     */
    public UpdateSourceHandler(final List<ReleaseUpdateSource> releaseHandlerList, final List<DevelopmentUpdateSource> developmentHandlerList) {
        this.releaseHandlerList = releaseHandlerList;
        this.developmentHandlerList = developmentHandlerList;
    }

    /**
     * Searches for updates in the provided {@link ReleaseUpdateSource} and {@link DevelopmentUpdateSource} lists
     * and returns the latest version with the URL to download it from.
     * If there is no update available, the URL in the pair is null.
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
        try {
            latest = searchUpdateFor(latest, releaseHandlerList, comparator, ReleaseUpdateSource::getReleaseVersions);
        } catch (final UnknownHostException e) {
            LOG.warn("The update server for release builds is currently not available!");
        } catch (final IOException e) {
            LOG.warn("Could not get the latest release build! " + e.getMessage(), e);
        }
        if (config.isDevDownloadEnabled() && !(latest.getValue() != null && config.isForcedStrategy())) {
            try {
                latest = searchUpdateFor(latest, developmentHandlerList, comparator, DevelopmentUpdateSource::getDevelopmentVersions);
            } catch (final UnknownHostException e) {
                LOG.warn("The update server for dev builds is currently not available!");
            } catch (final IOException e) {
                LOG.warn("Could not get the latest dev build! " + e.getMessage(), e);
            }
        }
        return latest;
    }

    private <T> Pair<Version, String>
    searchUpdateFor(final Pair<Version, String> latest, final List<T> updateSources, final VersionComparator comparator,
                    final UpdateSourceConsumer<T> consumer) throws IOException {
        Pair<Version, String> currentLatest = latest;
        for (final T updateSource : updateSources) {
            for (final Map.Entry<Version, String> entry : consumer.consume(updateSource).entrySet()) {
                if (comparator.isOtherNewerThanCurrent(latest.getKey(), entry.getKey())) {
                    currentLatest = Pair.of(entry.getKey(), entry.getValue());
                }
            }
        }
        return currentLatest;
    }

    /**
     * A consumer for {@link UpdateSource}s.
     *
     * @param <T> something implementing {@link UpdateSource}
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
