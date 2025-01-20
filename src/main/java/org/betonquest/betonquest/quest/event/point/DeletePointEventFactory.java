package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.event.DatabaseSaverStaticEvent;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.SequentialStaticEvent;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create delete points events from {@link Instruction}s.
 */
public class DeletePointEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * Create the delete points event factory.
     *
     * @param dataStorage the storage providing player data
     * @param saver       the saver to use
     */
    public DeletePointEventFactory(final PlayerDataStorage dataStorage, final Saver saver) {
        this.dataStorage = dataStorage;
        this.saver = saver;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new DeletePointEvent(dataStorage::getOffline, getCategory(instruction));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final String category = getCategory(instruction);
        return new SequentialStaticEvent(
                new OnlineProfileGroupStaticEventAdapter(
                        PlayerConverter::getOnlineProfiles,
                        new DeletePointEvent(dataStorage::getOffline, category)
                ),
                new DatabaseSaverStaticEvent(saver, () -> new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category))
        );
    }

    private String getCategory(final Instruction instruction) throws QuestException {
        return Utils.addPackage(instruction.getPackage(), instruction.next());
    }
}
