package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
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
        final OnlineProfile profile = mock(OnlineProfile.class);
        assertDoesNotThrow(() -> sender.sendNotification(profile), "Sending no notification should fail in no case.");
    }
}
