package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.database.Saver;

/**
 * A playerless event that executes a database query with the {@link Saver} when executed.
 */
public class DatabaseSaverPlayerlessEvent implements PlayerlessEvent {

    /**
     * The saver used to execute the database query.
     */
    private final Saver saver;

    /**
     * The method to create the record to save.
     */
    private final QuestSupplier<? extends Saver.Record> recordSupplier;

    /**
     * Create a playerless database saver event. The saver will be used to save the record created by the record supplier.
     *
     * @param saver          the saver to use
     * @param recordSupplier the record supplier
     */
    public DatabaseSaverPlayerlessEvent(final Saver saver, final QuestSupplier<? extends Saver.Record> recordSupplier) {
        this.saver = saver;
        this.recordSupplier = recordSupplier;
    }

    @Override
    public void execute() throws QuestException {
        saver.add(recordSupplier.get());
    }
}
