package org.betonquest.betonquest.modules.logger.custom.debug;

import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class.
 */
public class LogfileFormatter extends Formatter {

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
        final BetonQuestLogRecord logRecord = record instanceof BetonQuestLogRecord ? (BetonQuestLogRecord) record : null;
        final String plugin = "[" + (logRecord == null ? "?" : logRecord.getPlugin()) + "] ";
        final String questPackage = logRecord == null || logRecord.getPack().isEmpty() ? "" : "<" + logRecord.getPack() + "> ";
        final String message = formatMessage(record);
        final String throwable = formatThrowable(record);

        return String.format("[%1$ty.%1$tm.%1$td %tT %2$s]: %3$s%4$s%5$s%6$s%n",
                date, record.getLevel().getName(),
                plugin, questPackage, message,
                throwable);
    }

    /**
     * Formats a {@link LogRecord} to a readable string.
     *
     * @param record The record to format
     * @return The formatted string
     */
    protected String formatThrowable(final LogRecord record) {
        String throwable = "";
        if (record.getThrown() != null) {
            final StringWriter sWriter = new StringWriter();
            final PrintWriter pWriter = new PrintWriter(sWriter);
            pWriter.println();
            record.getThrown().printStackTrace(pWriter);
            pWriter.close();
            throwable = sWriter.toString();
        }
        return throwable;
    }
}
