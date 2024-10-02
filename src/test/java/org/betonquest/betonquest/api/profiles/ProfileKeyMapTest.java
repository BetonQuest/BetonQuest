package org.betonquest.betonquest.api.profiles;

import org.bukkit.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileKeyMapTest {

    private @Mock Server server;

    @Test
    void testNewMapHasSizeZero() {
        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        assertEquals(0, subject.size(), "A new map instance should be empty.");
    }

    @Test
    void testNewWithObjectHasSizeOne() {
        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(mockProfile(), new Object());

        assertEquals(1, subject.size(), "Map should have size equal to elements that have been put into it.");
    }

    @Test
    void testInitializedWithExternalHasSameSize() {
        final Map<UUID, Object> internal = new HashMap<>();
        internal.put(UUID.randomUUID(), new Object());

        final Map<Profile, Object> subject = new ProfileKeyMap<>(internal, server);

        assertEquals(internal.size(), subject.size(), "When initialized with a non-empty map the contents should still be accessible.");
    }

    @Test
    void testNewMapIsEmpty() {
        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        assertTrue(subject.isEmpty(), "Initialization without providing a map instance should create an empty map.");
    }

    @Test
    void testMapWithObjectIsNotEmpty() {
        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(mockProfile(), new Object());

        assertFalse(subject.isEmpty(), "The map should not be empty after a key-value pair was put into it.");
    }

    @Test
    void testContainsKeyFindsExisting() {
        final Profile key = mockProfile();
        final Object value = new Object();

        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(key, value);

        assertTrue(subject.containsKey(key), "containsKey() should be true when using the same key object as was used for putting a key-value pair into the map.");
    }

    @Test
    void testContainsKeyCannotFindDifferent() {
        final Profile key = mockProfile();
        final Profile other = mockProfile();
        final Object value = new Object();

        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(key, value);

        assertFalse(subject.containsKey(other), "containsKey() must not return true if no object with the key was put into the map.");
    }

    @Test
    void testContainsValueFindsValue() {
        final Profile key = mockProfile();
        final Object value = new Object();

        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(key, value);

        assertTrue(subject.containsValue(value), "");
    }

    @Test
    void testContainsValueCannotFindDifferent() {
        final Profile key = mockProfile();
        final Object value = new Object();
        final Object other = new Object();

        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);

        subject.put(key, value);

        assertFalse(subject.containsValue(other));
    }

    @Test
    void testGetReturnsValueFromInternalMap() {
        final UUID profileUuid = UUID.randomUUID();
        final Profile profile = mockProfile(profileUuid);
        final Object value = new Object();

        final Map<UUID, Object> internal = new HashMap<>();
        internal.put(profileUuid, value);

        final Map<Profile, Object> subject = new ProfileKeyMap<>(internal, server);

        final Object retrieved = subject.get(profile);

        assertSame(value, retrieved);
    }

    @Test
    void testGetWithWrongTypeDoesNotFail() {

        final Map<Profile, Object> subject = new ProfileKeyMap<>(server);
        subject.put(mockProfile(), new Object());

        final Object invalidKey = new Object();

        assertDoesNotThrow(() -> subject.get(invalidKey), "Using an incorrectly typed key must not throw an exception.");
    }

    @Test
    void testPutWritesValueToInternalMap() {
        final UUID profileUuid = UUID.randomUUID();
        final Profile profile = mockProfile(profileUuid);
        final Object value = new Object();

        final Map<UUID, Object> internal = new HashMap<>();
        final Map<Profile, Object> subject = new ProfileKeyMap<>(internal, server);

        subject.put(profile, value);

        assertSame(value, internal.get(profileUuid));
    }

    private static Profile mockProfile() {
        return mockProfile(UUID.randomUUID());
    }

    private static Profile mockProfile(final UUID profileUuid) {
        final Profile profile = mock(Profile.class, invocation -> fail("Unstubbed Profile method called."));
        doReturn(profileUuid).when(profile).getProfileUUID();
        return profile;
    }
}
