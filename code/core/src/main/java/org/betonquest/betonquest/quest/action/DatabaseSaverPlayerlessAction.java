package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.database.Saver;
import org.jetbrains.annotations.Nullable;

/**
 * A playerless action that executes a database query with the {@link Saver} when executed.
 */
public class DatabaseSaverPlayerlessAction implements PlayerlessAction {

    /**
     * Custom logger for debug messages.
     */
    @Nullable
    private final BetonQuestLogger log;

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
     * @param log            the logger to use
     * @param saver          the saver to use
     * @param recordSupplier the record supplier
     */
    public DatabaseSaverPlayerlessAction(@Nullable final BetonQuestLogger log, final Saver saver, final QuestSupplier<? extends Saver.Record> recordSupplier) {
        this.log = log;
        this.saver = saver;
        this.recordSupplier = recordSupplier;
    }

    /**
     * Create a playerless database saver action without custom logger.
     *
     * @param saver          the saver to use
     * @param recordSupplier the record supplier
     */
    public DatabaseSaverPlayerlessAction(final Saver saver, final QuestSupplier<? extends Saver.Record> recordSupplier) {
        this(null, saver, recordSupplier);
    }

    @Override
    public void execute() throws QuestException {
        final Saver.Record record = recordSupplier.get();
        if (log != null) {
            log.debug("Executing DatabaseSaverPlayerlessAction, adding record to saver: %s".formatted(record));
        }
        saver.add(record);
    }
}
