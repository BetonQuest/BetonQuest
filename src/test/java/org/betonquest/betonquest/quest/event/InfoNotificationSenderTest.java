package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link InfoNotificationSender}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class InfoNotificationSenderTest {
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testSendNotifyIsCalled(@Mock final QuestPackage questPackage) {
        when(questPackage.getQuestPath()).thenReturn("package.path");
        final NotificationSender sender = new InfoNotificationSender("message-name", questPackage, "full.id");

        try (MockedStatic<Config> config = mockStatic(Config.class)) {
            final Profile profile = getMockedProfile();
            assertTrue(profile.getOnlineProfile().isPresent(), "Profile should have an online profile.");
            sender.sendNotification(profile);
            config.verify(() -> Config.sendNotify("package.path", profile.getOnlineProfile().get(), "message-name", null, "message-name,info"));
        }
    }

    @Test
    void testSendNotifyHandlesError(@Mock final QuestPackage questPackage, final LogValidator logValidator) {
        when(questPackage.getQuestPath()).thenReturn("package.path");
        final NotificationSender sender = new InfoNotificationSender("message-name", questPackage, "full.id");

        try (MockedStatic<Config> config = mockStatic(Config.class)) {
            config.when(() -> Config.sendNotify(any(), any(OnlineProfile.class), any(), any(), any()))
                    .thenThrow(new QuestRuntimeException("Test cause."));
            assertDoesNotThrow(() -> sender.sendNotification(getMockedProfile()), "Failing to send a notification should not throw an exception.");
        }
        logValidator.assertLogEntry(Level.WARNING, "The notify system was unable to play a sound for the 'message-name' category in 'full.id'. Error was: 'Test cause.'");
        logValidator.assertLogEntry(Level.FINE, "Additional stacktrace:", QuestRuntimeException.class, "Test cause.");
        logValidator.assertEmpty();
    }

    private Profile getMockedProfile() {
        final Profile profile = mock(Profile.class);
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        when(profile.getOnlineProfile()).thenReturn(Optional.of(onlineProfile));
        return profile;
    }
}
