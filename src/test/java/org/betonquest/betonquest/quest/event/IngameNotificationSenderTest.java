package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link IngameNotificationSender}.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(BetonQuestLoggerService.class)
class IngameNotificationSenderTest {

    @Test
    void testSendNotifyIsCalled(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) throws QuestException {
        final PlayerDataStorage playerDataStorage = getPlayerDataStorage();
        final PluginMessage pluginMessage = getPluginMessage();

        final NotificationSender sender = new IngameNotificationSender(logger, playerDataStorage, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Notify> notify = mockStatic(Notify.class)) {
            final NotifyIO notifyIO = mock(NotifyIO.class);
            notify.when(() -> Notify.get(questPackage, "message-name,info")).thenReturn(notifyIO);
            final Profile profile = getMockedProfile();
            assertTrue(profile.getOnlineProfile().isPresent(), "Profile should have an online profile.");
            sender.sendNotification(profile);
            verify(notifyIO, times(1)).sendNotify("test message", profile.getOnlineProfile().get());
        }
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSendNotifyHandlesError(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) throws QuestException {
        final PlayerDataStorage playerDataStorage = getPlayerDataStorage();
        final PluginMessage pluginMessage = getPluginMessage();

        final NotificationSender sender = new IngameNotificationSender(logger, playerDataStorage, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Notify> notify = mockStatic(Notify.class)) {
            final NotifyIO notifyIO = mock(NotifyIO.class);
            notify.when(() -> Notify.get(questPackage, "message-name,info")).thenReturn(notifyIO);
            doThrow(new QuestException("Test cause.")).when(notifyIO).sendNotify(any(), any());
            assertDoesNotThrow(() -> sender.sendNotification(getMockedProfile()), "Failing to send a notification should not throw an exception.");
            verify(logger, times(1)).warn(eq(questPackage), eq("The notify system was unable to play a sound for the 'message-name' message in 'full.id'. Error was: 'Test cause.'"), any(QuestException.class));
            verifyNoMoreInteractions(logger);
        }
    }

    private Profile getMockedProfile() {
        final Profile profile = mock(Profile.class);
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        when(profile.getOnlineProfile()).thenReturn(Optional.of(onlineProfile));
        return profile;
    }

    private PlayerDataStorage getPlayerDataStorage() {
        final PlayerDataStorage playerDataStorage = mock(PlayerDataStorage.class);
        final PlayerData playerData = mock(PlayerData.class);
        when(playerData.getLanguage()).thenReturn("en");
        when(playerDataStorage.get(any())).thenReturn(playerData);
        return playerDataStorage;
    }

    private PluginMessage getPluginMessage() {
        final PluginMessage pluginMessage = mock(PluginMessage.class);
        when(pluginMessage.getMessage("en", "message-name")).thenReturn("test message");
        return pluginMessage;
    }
}
