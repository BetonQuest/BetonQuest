package org.betonquest.betonquest.modules.logger.handler;

import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

/**
 * A selector that selects players (by UUID) who should receive a specific {@link LogRecord}.
 */
public interface RecordReceiverSelector {

    /**
     * Find players that should reveice the given LogRecord.
     *
     * @param record the record to filter for
     * @return a set of players that should reveive the record
     */
    Set<UUID> findReceivers(LogRecord record);
}
