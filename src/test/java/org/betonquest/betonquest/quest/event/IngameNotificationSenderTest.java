package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
        final PluginMessage pluginMessage = mock(PluginMessage.class);
        final NotificationSender sender = new IngameNotificationSender(logger, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        final Profile profile = getMockedProfile();
        assertTrue(profile.getOnlineProfile().isPresent(), "Profile should have an online profile.");
        sender.sendNotification(profile);
        verify(pluginMessage, times(1)).sendNotify(questPackage, profile.getOnlineProfile().get(), "message-name", "message-name,info");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSendNotifyHandlesError(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) throws QuestException {
        final PluginMessage pluginMessage = mock(PluginMessage.class);
        final NotificationSender sender = new IngameNotificationSender(logger, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        doThrow(new QuestException("Test cause.")).when(pluginMessage).sendNotify(any(), any(OnlineProfile.class), any(), any());
        assertDoesNotThrow(() -> sender.sendNotification(getMockedProfile()), "Failing to send a notification should not throw an exception.");
        verify(logger, times(1)).warn(eq(questPackage), eq("The notify system was unable to play a sound for the 'message-name' message in 'full.id'. Error was: 'Test cause.'"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }

    private Profile getMockedProfile() {
        final Profile profile = mock(Profile.class);
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        when(profile.getOnlineProfile()).thenReturn(Optional.of(onlineProfile));
        return profile;
    }
}
