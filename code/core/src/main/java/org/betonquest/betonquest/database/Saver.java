package org.betonquest.betonquest.database;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * The saver is used to save data via records into the database. Implementations should be thread save and must document
 * if they are not.
 */
public interface Saver {
    /**
     * Adds new record to the queue, where it will be saved to the database.
     *
     * @param rec Record to save
     */
    void add(Record rec);

    /**
     * Ends this saver's job, letting it save all remaining data.
     */
    void end();

    /**
     * Holds the data and the method of saving them to the database.
     */
    record Record(UpdateType type, String... args) {
        /**
         * Creates new Record, which can be saved to the database using
         * {@code Saver.add()}.
         *
         * @param type method used for saving the data
         * @param args list of Strings which will be saved to the database
         */
        public Record(final UpdateType type, @Nullable final String... args) {
            this.type = type;
            this.args = Arrays.copyOf(args, args.length);
        }
    }
}
