package org.betonquest.betonquest.utils.logger.custom;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class.
 */
public class DebugLogFormatter extends Formatter {

    /**
     * The log report's timestamp.
     */
    private final Date dat = new Date();

    /**
     * Default constructor.
     */
    public DebugLogFormatter() {
        super();
    }

    @Override
    public String format(final LogRecord record) {
        dat.setTime(record.getMillis());
        final String message = formatMessage(record);
        final String throwable = formatThrowable(record);

        return String.format("[%1$ty.%1$tm.%1$td %tT %2$s]: %3$s%4$s%n",
                dat,
                record.getLevel().getName(),
                message,
                throwable);
    }

    private String formatThrowable(final LogRecord record) {
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
