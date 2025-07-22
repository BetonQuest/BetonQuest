package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.id.JournalEntryID;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Represents the journal pointer.
 *
 * @param pointer   String pointing to the journal entry.
 * @param timestamp Timestamp indicating date of this entry.
 */
public record Pointer(JournalEntryID pointer, long timestamp) {
    /**
     * The minimum length to parse hours.
     */
    private static final int HOUR_LENGTH = 2;

    /**
     * Generates a date prefix with the timestamp.
     *
     * @param config the config to load formatting values from
     * @return the formatted date
     */
    public String generateDatePrefix(final ConfigAccessor config) {
        final String date = new SimpleDateFormat(config.getString("date_format"), Locale.ROOT).format(timestamp);
        final String[] dateParts = date.split(" ");
        final String day = "§" + config.getString("journal.format.color.date.day") + dateParts[0];
        final String hour;
        if (dateParts.length >= HOUR_LENGTH) {
            hour = "§" + config.getString("journal.format.color.date.hour") + dateParts[1];
        } else {
            hour = "";
        }
        return day + " " + hour;
    }
}
