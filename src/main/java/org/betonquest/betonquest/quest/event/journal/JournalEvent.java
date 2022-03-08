package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.quest.event.NotificationSender;

public class JournalEvent implements Event {

    private final JournalChanger journalChanger;
    private final NotificationSender notificationSender;

    public JournalEvent(final JournalChanger journalChanger, final NotificationSender notificationSender) {
        this.journalChanger = journalChanger;
        this.notificationSender = notificationSender;
    }

    @Override
    public void execute(final String playerId) {
        final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(playerId);
        final Journal journal = playerData.getJournal();
        journalChanger.changeJournal(journal);
        journal.update();
        notificationSender.sendNotification(playerId);
    }
}
