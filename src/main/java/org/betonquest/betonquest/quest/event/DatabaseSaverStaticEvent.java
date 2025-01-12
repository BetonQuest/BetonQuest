package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.function.Supplier;

/**
 * A "static" event that executes a database query with the {@link Saver} when executed.
 */
public class DatabaseSaverStaticEvent implements StaticEvent {

    /**
     * The saver used to execute the database query.
     */
    private final Saver saver;

    /**
     * The method to create the record to save.
     */
    private final Supplier<? extends Saver.Record> recordSupplier;

    /**
     * Create a "static" database saver event. The saver will be used to save the record created by the record supplier.
     *
     * @param saver          the saver to use
     * @param recordSupplier the record supplier
     */
    public DatabaseSaverStaticEvent(final Saver saver, final Supplier<? extends Saver.Record> recordSupplier) {
        this.saver = saver;
        this.recordSupplier = recordSupplier;
    }

    @Override
    public void execute() throws QuestException {
        saver.add(recordSupplier.get());
    }
}
