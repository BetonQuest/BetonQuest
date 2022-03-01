package org.betonquest.betonquest.events.action.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.events.action.EventAction;
import org.betonquest.betonquest.events.action.NotificationSender;

public class JournalEventAction implements EventAction {

    private final JournalChanger journalChanger;
    private final NotificationSender notificationSender;

    public JournalEventAction(final JournalChanger journalChanger, final NotificationSender notificationSender) {
        this.journalChanger = journalChanger;
        this.notificationSender = notificationSender;
    }

    @Override
    public void doAction(final String playerId) {
        final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(playerId);
        final Journal journal = playerData.getJournal();
        journalChanger.changeJournal(journal);
        journal.update();
        notificationSender.sendNotification(playerId);
    }
}
