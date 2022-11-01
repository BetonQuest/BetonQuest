package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link NoNotificationSender}.
 */
class NoNotificationSenderTest {
    @Test
    void testSendNotificationDoesNothing() {
        final NoNotificationSender sender = new NoNotificationSender();
        final Profile profile = mock(Profile.class);
        assertDoesNotThrow(() -> sender.sendNotification(profile.getOnlineProfile()), "Sending no notification should fail in no case.");
    }
}
