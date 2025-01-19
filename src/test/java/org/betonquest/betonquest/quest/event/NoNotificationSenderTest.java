package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.OnlineProfile;
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
        final OnlineProfile onlineProfile = mock(OnlineProfile.class);
        assertDoesNotThrow(() -> sender.sendNotification(onlineProfile), "Sending no notification should fail in no case.");
    }
}
