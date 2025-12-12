package org.betonquest.betonquest.logger.format;

import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class.
 */
public final class LogfileFormatter extends Formatter {

    /**
     * The formatter for the logs timestamp.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss", Locale.ROOT);

    /**
     * Default constructor.
     */
    public LogfileFormatter() {
        super();
    }

    @Override
    public String format(final LogRecord record) {
        final ZonedDateTime time = Instant.ofEpochMilli(record.getMillis()).atZone(ZoneId.systemDefault());
        final String formattedTime = FORMATTER.format(time);

        final Optional<BetonQuestLogRecord> betonRecord = BetonQuestLogRecord.safeCast(record);
        final String plugin = "[" + betonRecord.map(BetonQuestLogRecord::getPlugin).orElse("?") + "] ";
        final String logSourcePath = betonRecord
                .map(BetonQuestLogRecord::getLogSource)
                .map(LogSource::getSourcePath)
                .map(source -> "<" + source + "> ")
                .orElse("");
        final String message = formatMessage(record);
        final String throwable = record.getThrown() == null ? "" : FormatterUtils.formatThrowable(record.getThrown());

        return String.format("[%s %s]: %s%s%s%s%n",
                formattedTime, record.getLevel().getName(), plugin, logSourcePath, message, throwable);
    }
}
