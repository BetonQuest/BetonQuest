package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * The journal event, doing what was defined in its instruction.
 */
public class JournalEvent implements Event {

    /**
     * BetonQuest instance used to get the {@link PlayerData}.
     */
    private final BetonQuest betonQuest;

    /**
     * Change to apply to a journal when the event is executed.
     */
    private final JournalChanger journalChanger;

    /**
     * Notification to send after the journal was changed.
     */
    private final NotificationSender notificationSender;

    /**
     * Create a journal event.
     *
     * @param betonQuest         BetonQuest instance
     * @param journalChanger     change to apply to a journal
     * @param notificationSender notification to send
     */
    public JournalEvent(final BetonQuest betonQuest, final JournalChanger journalChanger, final NotificationSender notificationSender) {
        this.betonQuest = betonQuest;
        this.journalChanger = journalChanger;
        this.notificationSender = notificationSender;
    }

    @Override
    public void execute(final Profile profile) {
        final PlayerData playerData = betonQuest.getOfflinePlayerData(profile);
        final Journal journal = playerData.getJournal();
        journalChanger.changeJournal(journal);
        journal.update();
        notificationSender.sendNotification(profile.getOnlineProfile());
    }
}
