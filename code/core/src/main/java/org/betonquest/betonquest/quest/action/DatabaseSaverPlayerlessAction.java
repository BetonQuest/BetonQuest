package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.database.Saver;

/**
 * A playerless action that executes a database query with the {@link Saver} when executed.
 */
public class DatabaseSaverPlayerlessAction implements PlayerlessAction {

    /**
     * The saver used to execute the database query.
     */
    private final Saver saver;

    /**
     * The method to create the record to save.
     */
    private final QuestSupplier<? extends Saver.Record> recordSupplier;

    /**
     * Create a playerless database saver action. The saver will be used to save the record created by the record supplier.
     *
     * @param saver          the saver to use
     * @param recordSupplier the record supplier
     */
    public DatabaseSaverPlayerlessAction(final Saver saver, final QuestSupplier<? extends Saver.Record> recordSupplier) {
        this.saver = saver;
        this.recordSupplier = recordSupplier;
    }

    @Override
    public void execute() throws QuestException {
        saver.add(recordSupplier.get());
    }
}
