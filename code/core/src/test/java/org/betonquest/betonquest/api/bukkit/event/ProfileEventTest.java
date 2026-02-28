package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ProfileEvent}.
 */
class ProfileEventTest {

    @Test
    void testGetProfile() {
        final Profile profile = mock(Profile.class);
        final ProfileEvent profileEvent = new ProfileEventMock(profile);
        assertEquals(profile, profileEvent.getProfile(), "getProfile should return the profile");
        assertFalse(profileEvent.isAsynchronous(), "isAsynchronous should return false");
    }

    @Test
    void testGetProfileAsync() {
        final Profile profile = mock(Profile.class);
        final ProfileEvent profileEvent = new ProfileEventMock(profile, true);
        assertEquals(profile, profileEvent.getProfile(), "getProfile should return the profile");
        assertTrue(profileEvent.isAsynchronous(), "isAsynchronous should return true");
    }

    @Test
    void testGetProfileSync() {
        final Profile profile = mock(Profile.class);
        final ProfileEvent profileEvent = new ProfileEventMock(profile, false);
        assertEquals(profile, profileEvent.getProfile(), "getProfile should return the profile");
        assertFalse(profileEvent.isAsynchronous(), "isAsynchronous should return false");
    }

    /**
     * Test implementation of {@link ProfileEvent}.
     */
    private static final class ProfileEventMock extends ProfileEvent {

        private ProfileEventMock(final Profile who) {
            super(who);
        }

        private ProfileEventMock(final Profile who, final boolean isAsync) {
            super(who, isAsync);
        }

        @Override
        public HandlerList getHandlers() {
            return new HandlerList();
        }
    }
}
