package org.betonquest.betonquest.logger.format;

import org.betonquest.betonquest.logger.BetonQuestLogRecord;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class.
 */
public final class LogfileFormatter extends Formatter {

    /**
     * The log report's timestamp.
     */
    private final Date date = new Date();

    /**
     * Default constructor.
     */
    public LogfileFormatter() {
        super();
    }

    @Override
    public String format(final LogRecord record) {
        date.setTime(record.getMillis());
        final Optional<BetonQuestLogRecord> betonRecord = BetonQuestLogRecord.safeCast(record);
        final String plugin = "[" + betonRecord.map(BetonQuestLogRecord::getPlugin).orElse("?") + "] ";
        final String questPackage = betonRecord
                .flatMap(BetonQuestLogRecord::getPack)
                .map(pack -> "<" + pack + "> ")
                .orElse("");
        final String message = formatMessage(record);
        final String throwable = record.getThrown() == null ? "" : FormatterUtils.formatThrowable(record.getThrown());

        return String.format("[%1$ty.%1$tm.%1$td %tT %2$s]: %3$s%4$s%5$s%6$s%n",
                date, record.getLevel().getName(),
                plugin, questPackage, message,
                throwable);
    }
}
