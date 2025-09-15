package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link JournalEvent}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class JournalEventTest {
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testJournalEventChangesUpdatesAndNotifiesInOrder(
            @Mock final PlayerDataStorage dataStorage, @Mock final PlayerData data, @Mock final Journal journal,
            @Mock final JournalChanger changer, @Mock final NotificationSender sender) throws QuestException {
        final ProfileProvider profileProvider = mock(ProfileProvider.class);
        final OnlineProfile onlineProfile = profileProvider.getProfile(mock(Player.class));
        when(dataStorage.getOffline(onlineProfile)).thenReturn(data);
        when(data.getJournal()).thenReturn(journal);

        final JournalEvent event = new JournalEvent(dataStorage, changer, sender);

        event.execute(onlineProfile);

        final InOrder order = inOrder(journal, changer, sender);
        order.verify(changer).changeJournal(journal, onlineProfile);
        order.verify(journal).update();
        order.verify(sender).sendNotification(onlineProfile);
    }
}
