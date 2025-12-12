package org.betonquest.betonquest.logger.handler.chat;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A {@link RecordReceiverSelector} that selects specific players for all records that match the originating log source
 * and are not below a certain logging level.
 */
public class PlayerLogSourceReceiverSelector implements RecordReceiverSelector {

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
     * {@link LogSource} filter needed to match.
     */
    private final Predicate<String> filter;

    /**
     * Create a selector for the given set of players, filtering by the given minimum level and the log source filter.
     *
     * @param receivers    players to select on match
     * @param minimumLevel minimum level required for a match
     * @param filter       filter to match the source
     */
    public PlayerLogSourceReceiverSelector(final Set<UUID> receivers, final Level minimumLevel, final Predicate<String> filter) {
        this.receivers = receivers;
        this.minimumLevel = minimumLevel;
        this.filter = filter;
    }

    /**
     * Create a selector for the given set of players, filtering by the given minimum level and matching the log source
     * with the given pattern.
     * <p>
     * When the pattern...
     * <ol>
     *     <li>
     *         is exactly an asterisk then every source matches (including "no source" in case the log record isn't a
     *         {@link BetonQuestLogRecord}),
     *     </li>
     *     <li>
     *         ends on an asterisk, then every source having the rest of the pattern as prefix match or
     *     </li>
     *     <li>
     *         otherwise only exactly the same source matches.
     *     </li>
     * </ol>
     *
     * @param receivers    players to select on match
     * @param minimumLevel minimum level required for a match
     * @param pattern      pattern to match
     */
    public PlayerLogSourceReceiverSelector(final Set<UUID> receivers, final Level minimumLevel, final String pattern) {
        this(receivers, minimumLevel, createSourceFilter(pattern));
    }

    private static Predicate<String> createSourceFilter(final String pattern) {
        if (ALL_SELECTOR.equals(pattern)) {
            return pack -> true;
        }
        if (pattern.endsWith(ALL_SELECTOR)) {
            final String prefix = StringUtils.chop(pattern);
            return pack -> pack.startsWith(prefix);
        }
        return pack -> pack.equals(pattern);
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
                && isSourceIncluded(extractSourcePath(record));
    }

    private String extractSourcePath(final LogRecord record) {
        return BetonQuestLogRecord
                .safeCast(record)
                .map(BetonQuestLogRecord::getLogSource)
                .map(LogSource::getSourcePath)
                .orElse("");
    }

    private boolean isLevelIncluded(final Level level) {
        return level.intValue() >= minimumLevel.intValue();
    }

    private boolean isSourceIncluded(final String pack) {
        return filter.test(pack);
    }
}
