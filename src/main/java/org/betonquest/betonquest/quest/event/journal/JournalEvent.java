package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * The journal event, doing what was defined in its instruction.
 */
public class JournalEvent implements PlayerEvent {

    /**
     * Storage used to get the {@link PlayerData}.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Plugin Message instance to create the journal.
     */
    private final PluginMessage pluginMessage;

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
     * @param dataStorage        to get player data
     * @param pluginMessage      the plugin message to generate a new journal
     * @param journalChanger     change to apply to a journal
     * @param notificationSender notification to send
     */
    public JournalEvent(final PlayerDataStorage dataStorage, final PluginMessage pluginMessage, final JournalChanger journalChanger, final NotificationSender notificationSender) {
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.journalChanger = journalChanger;
        this.notificationSender = notificationSender;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData playerData = dataStorage.getOffline(profile);
        final Journal journal = playerData.getJournal(pluginMessage);
        journalChanger.changeJournal(journal, profile);
        journal.update();
        notificationSender.sendNotification(profile);
    }
}
