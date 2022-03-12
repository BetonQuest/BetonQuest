package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Static journal event to remove an entry from all players.
 */
public class RemoveJournalEntryStaticEvent implements StaticEvent {
    /**
     * The name of journal entry to remove.
     */
    private final String entryName;

    /**
     * The journal event that removes the entry from an online player.
     */
    private final JournalEvent deleteEvent;

    /**
     * Create the static remove event.
     *
     * @param entryName journal entry to remove
     * @param deleteEvent delete event to execute for all online players
     */
    public RemoveJournalEntryStaticEvent(final String entryName, final JournalEvent deleteEvent) {
        this.entryName = entryName;
        this.deleteEvent = deleteEvent;
    }

    @Override
    public void execute() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final String playerId = PlayerConverter.getID(p);
            deleteEvent.execute(playerId);
        }
        BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, entryName));
    }
}
