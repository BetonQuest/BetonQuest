package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.event.DatabaseSaverStaticEvent;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.SequentialStaticEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to create delete points events from {@link Instruction}s.
 */
public class DeletePointEventFactory implements EventFactory, StaticEventFactory {
    /**
     * BetonQuest plugin instance needed for building the point event.
     */
    private final BetonQuest betonQuest;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * Create the delete points event factory.
     *
     * @param betonQuest the plugin to use
     * @param saver      the saver to use
     */
    public DeletePointEventFactory(final BetonQuest betonQuest, final Saver saver) {
        this.betonQuest = betonQuest;
        this.saver = saver;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new DeletePointEvent(betonQuest::getOfflinePlayerData, getCategory(instruction));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final String category = getCategory(instruction);
        return new SequentialStaticEvent(
                new OnlineProfileGroupStaticEventAdapter(
                        PlayerConverter::getOnlineProfiles,
                        new DeletePointEvent(betonQuest::getOfflinePlayerData, category)
                ),
                new DatabaseSaverStaticEvent(saver, () -> new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category))
        );
    }

    private static @NotNull String getCategory(final Instruction instruction) throws QuestException {
        return Utils.addPackage(instruction.getPackage(), instruction.next());
    }
}
