package org.betonquest.betonquest.quest.event;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.config.PluginMessage;
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
        final Profile profile = getMockedProfile();
        final PluginMessage pluginMessage = getPluginMessage();

        final NotificationSender sender = new IngameNotificationSender(logger, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Notify> notify = mockStatic(Notify.class)) {
            final NotifyIO notifyIO = mock(NotifyIO.class);
            notify.when(() -> Notify.get(questPackage, "message-name,info")).thenReturn(notifyIO);

            assertTrue(profile.getOnlineProfile().isPresent(), "Profile should have an online profile.");
            sender.sendNotification(profile);
            final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
            verify(notifyIO, times(1)).sendNotify(any(Component.class), eq(onlineProfile));
        }
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSendNotifyHandlesError(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) throws QuestException {
        final Profile profile = getMockedProfile();
        final PluginMessage pluginMessage = getPluginMessage();

        final NotificationSender sender = new IngameNotificationSender(logger, pluginMessage, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Notify> notify = mockStatic(Notify.class)) {
            final NotifyIO notifyIO = mock(NotifyIO.class);
            notify.when(() -> Notify.get(questPackage, "message-name,info")).thenReturn(notifyIO);
            doThrow(new QuestException("Test cause.")).when(notifyIO).sendNotify(any(Component.class), any());

            assertDoesNotThrow(() -> sender.sendNotification(profile), "Failing to send a notification should not throw an exception.");
            verify(logger, times(1)).warn(eq(questPackage), eq("The notify system was unable to send the notification message 'message-name' in 'full.id'. Error was: 'Test cause.'"), any(QuestException.class));
            verifyNoMoreInteractions(logger);
        }
    }

    private Profile getMockedProfile() {
        final Profile profile = mock(Profile.class);
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        when(profile.getOnlineProfile()).thenReturn(Optional.of(onlineProfile));
        return profile;
    }

    private PluginMessage getPluginMessage() throws QuestException {
        final PluginMessage pluginMessage = mock(PluginMessage.class);
        final Component message = mock(Component.class);
        when(pluginMessage.getMessage(any(Profile.class), eq("message-name"))).thenReturn(message);
        return pluginMessage;
    }
}
