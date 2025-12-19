package org.betonquest.betonquest.feature.journal;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.text.TextParser;
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
     * @param textParser the text parser to use for formatting
     * @param config     the config to load formatting values from
     * @return the formatted date
     * @throws QuestException if parsing the date fails
     */
    public Component generateDatePrefix(final TextParser textParser, final ConfigAccessor config) throws QuestException {
        final String date = new SimpleDateFormat(config.getString("date_format"), Locale.ROOT).format(timestamp);
        final String[] dateParts = date.split(" ");
        final Component day = textParser.parse(config.getString("journal.format.color.date.day")).append(Component.text(dateParts[0]));
        final Component hour;
        if (dateParts.length >= HOUR_LENGTH) {
            hour = textParser.parse(config.getString("journal.format.color.date.hour")).append(Component.text(dateParts[1]));
        } else {
            hour = Component.empty();
        }
        return Component.empty().append(day).append(Component.space()).append(hour);
    }
}
