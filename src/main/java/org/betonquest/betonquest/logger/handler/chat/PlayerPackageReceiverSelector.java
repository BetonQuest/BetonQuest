package org.betonquest.betonquest.logger.handler.chat;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A {@link RecordReceiverSelector} that selects specific players for all records that match the originating package
 * and are not below a certain logging level.
 */
public class PlayerPackageReceiverSelector implements RecordReceiverSelector {
    /**
     * The simple regex 'all' selector.
     */
    private static final String ALL_SELECTOR = "*";

    /**
     * Players to select on a match.
     */
    private final Set<UUID> receivers;

    /**
     * Minimum level needed to match.
     */
    private final Level minimumLevel;

    /**
     * Package filter needed to match.
     */
    private final Predicate<String> packageFilter;

    /**
     * Create a selector for the given set of players, filtering by the given minimum level and the package filter.
     *
     * @param receivers     players to select on match
     * @param minimumLevel  minimum level required for a match
     * @param packageFilter package filter to match
     */
    public PlayerPackageReceiverSelector(final Set<UUID> receivers, final Level minimumLevel, final Predicate<String> packageFilter) {
        this.receivers = receivers;
        this.minimumLevel = minimumLevel;
        this.packageFilter = packageFilter;
    }

    /**
     * Create a selector for the given set of players, filtering by the given minimum level and matching the package
     * with the given pattern.
     * <p>
     * When the pattern...
     * <ol>
     *     <li>
     *         is exactly an asterisk then every package matches (including "no package" in case the log record isn't a
     *         {@link BetonQuestLogRecord}),
     *     </li>
     *     <li>
     *         ends on an asterisk then every package having the rest of the pattern as prefix match or
     *     </li>
     *     <li>
     *         otherwise only exactly the same package matches.
     *     </li>
     * </ol>
     * A)
     *
     * @param receivers      players to select on match
     * @param minimumLevel   minimum level required for a match
     * @param packagePattern package pattern to match
     */
    public PlayerPackageReceiverSelector(final Set<UUID> receivers, final Level minimumLevel, final String packagePattern) {
        this(receivers, minimumLevel, createPackageFilter(packagePattern));
    }

    private static Predicate<String> createPackageFilter(final String packagePattern) {
        if (ALL_SELECTOR.equals(packagePattern)) {
            return pack -> true;
        }
        if (packagePattern.endsWith(ALL_SELECTOR)) {
            final String prefix = StringUtils.chop(packagePattern);
            return pack -> pack.startsWith(prefix);
        }
        return pack -> pack.equals(packagePattern);
    }

    @Override
    public Set<UUID> findReceivers(final LogRecord record) {
        if (match(record)) {
            return receivers;
        }
        return Collections.emptySet();
    }

    private boolean match(final LogRecord record) {
        return isLevelIncluded(record.getLevel())
                && isPackageIncluded(extractPackage(record));
    }

    private String extractPackage(final LogRecord record) {
        return BetonQuestLogRecord
                .safeCast(record)
                .flatMap(BetonQuestLogRecord::getPack)
                .orElse("");
    }

    private boolean isLevelIncluded(final Level level) {
        return level.intValue() >= minimumLevel.intValue();
    }

    private boolean isPackageIncluded(final String pack) {
        return packageFilter.test(pack);
    }
}
