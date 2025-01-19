package org.betonquest.betonquest.logger.format;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Shared code for all log Formatter used by BetonQuest.
 */
public final class FormatterUtils {

    /**
     * Private utility class constructor.
     */
    private FormatterUtils() {
    }

    /**
     * Creates a string for logging from a {@link Throwable}.
     *
     * @param throwable throwable to format
     * @return the formatted string
     */
    public static String formatThrowable(final Throwable throwable) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        pWriter.println();
        throwable.printStackTrace(pWriter);
        pWriter.close();
        return sWriter.toString();
    }
}
