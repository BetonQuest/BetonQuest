package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
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
    void testSendNotifyIsCalled(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) {
        final NotificationSender sender = new IngameNotificationSender(logger, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Config> config = mockStatic(Config.class)) {
            final Profile profile = getMockedProfile();
            assertTrue(profile.getOnlineProfile().isPresent(), "Profile should have an online profile.");
            sender.sendNotification(profile);
            config.verify(() -> Config.sendNotify(questPackage, profile.getOnlineProfile().get(), "message-name", new String[0], "message-name,info"));
        }
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSendNotifyHandlesError(final BetonQuestLogger logger, @Mock final QuestPackage questPackage) {
        final NotificationSender sender = new IngameNotificationSender(logger, questPackage, "full.id", NotificationLevel.INFO, "message-name");

        try (MockedStatic<Config> config = mockStatic(Config.class)) {
            config.when(() -> Config.sendNotify(any(), any(OnlineProfile.class), any(), any(), any()))
                    .thenThrow(new QuestRuntimeException("Test cause."));
            assertDoesNotThrow(() -> sender.sendNotification(getMockedProfile()), "Failing to send a notification should not throw an exception.");
        }
        verify(logger, times(1)).warn(eq(questPackage), eq("The notify system was unable to play a sound for the 'message-name' message in 'full.id'. Error was: 'Test cause.'"), any(QuestRuntimeException.class));
        verifyNoMoreInteractions(logger);
    }

    private Profile getMockedProfile() {
        final Profile profile = mock(Profile.class);
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        when(profile.getOnlineProfile()).thenReturn(Optional.of(onlineProfile));
        return profile;
    }
}
