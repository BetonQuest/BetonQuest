package org.betonquest.betonquest.events.action.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.events.action.EventAction;
import org.betonquest.betonquest.events.action.EventBulkAction;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RemoveJournalEntryEventBulkAction implements EventBulkAction {
    private final String entryName;
    private final EventAction deleteSingleAction;

    public RemoveJournalEntryEventBulkAction(final String entryName, final EventAction deleteSingleAction) {
        this.entryName = entryName;
        this.deleteSingleAction = deleteSingleAction;
    }

    @Override
    public void doBulkAction() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final String playerId = PlayerConverter.getID(p);
            deleteSingleAction.doAction(playerId);
        }
        BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, entryName));
    }
}
