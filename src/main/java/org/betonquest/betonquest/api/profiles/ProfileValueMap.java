package org.betonquest.betonquest.api.profiles;

import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is used to map profiles as values.
 * It saves the UUID of the profile instead of the profile itself and converts it to
 * profiles on return. It also takes profiles as input
 *
 * @param <T> the type of the object to be mapped
 */
public class ProfileValueMap<T> extends AbstractMap<T, Profile> implements Map<T, Profile> {

    /**
     * The map used to store the profile's UUIDs.
     */
    private final Map<T, UUID> profileMap;
    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new profile value mapping.
     *
     * @param server the server instance
     */
    public ProfileValueMap(final Server server) {
        this(new HashMap<>(), server);
    }

    public ProfileValueMap(final Map<T, UUID> profileMap, final Server server) {
        this.profileMap = profileMap;
        this.server = server;
    }

    @Override
    public int size() {
        return profileMap.size();
    }

    @Override
    public boolean isEmpty() {
        return profileMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return profileMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        if (value instanceof Profile profile) {
            return profileMap.containsValue(profile.getProfileUUID());
        }
        return false;
    }

    @Override
    public Profile get(final Object key) {
        return toProfileOrNull(profileMap.get(key));
    }

    @Nullable
    @Override
    public Profile put(final T key, final Profile value) {
        profileMap.put(key, value.getProfileUUID());
        return value;
    }

    @Override
    public Profile remove(final Object key) {
        return toProfileOrNull(profileMap.remove(key));
    }

    @Override
    public void putAll(@NotNull final Map<? extends T, ? extends Profile> map) {
        final Map<? extends T, UUID> entries = map.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getProfileUUID()));
        profileMap.putAll(entries);
    }

    @Override
    public void clear() {
        profileMap.clear();
    }

    @NotNull
    @Override
    public Set<T> keySet() {
        return profileMap.keySet();
    }

    @NotNull
    @Override
    public Set<Entry<T, Profile>> entrySet() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Profile toProfileOrNull(final UUID profileId) {
        if (profileId == null) {
            return null;
        }
        return PlayerConverter.getID(server.getPlayer(profileId));
    }

    final class EntrySet extends AbstractSet<Entry<T, Profile>> {

        @Override
        public Iterator<Entry<T, Profile>> iterator() {
            return null;
        }

        @Override
        public int size() {
            return profileMap.size();
        }

        @Override
        public boolean contains(final Object o) {
            return super.contains(o);
        }

        @Override
        public boolean remove(final Object o) {
            return super.remove(o);
        }

        @Override
        public void clear() {
            profileMap.clear();
        }
    }
}
