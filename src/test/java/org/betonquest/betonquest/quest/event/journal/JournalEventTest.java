package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link JournalEvent}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class JournalEventTest {
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testJournalEventChangesUpdatesAndNotifiesInOrder(
            @Mock final BetonQuest betonQuest, @Mock final PlayerData data, @Mock final Journal journal,
            @Mock final JournalChanger changer, @Mock final NotificationSender sender) {
        final String playerId = "fake-id";
        when(betonQuest.getOfflinePlayerData(playerId)).thenReturn(data);
        when(data.getJournal()).thenReturn(journal);

        final JournalEvent event = new JournalEvent(betonQuest, changer, sender);

        event.execute(playerId);

        final InOrder order = inOrder(journal, changer, sender);
        order.verify(changer).changeJournal(journal);
        order.verify(journal).update();
        order.verify(sender).sendNotification(playerId);
    }
}
