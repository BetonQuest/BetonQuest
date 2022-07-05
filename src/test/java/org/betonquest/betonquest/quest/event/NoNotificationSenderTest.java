package org.betonquest.betonquest.quest.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link NoNotificationSender}.
 */
class NoNotificationSenderTest {
    @Test
    void testSendNotificationDoesNothing() {
        final NoNotificationSender sender = new NoNotificationSender();
        assertDoesNotThrow(() -> sender.sendNotification("fake-id"), "Sending no notification should fail in no case.");
    }
}
