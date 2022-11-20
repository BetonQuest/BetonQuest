package org.betonquest.betonquest.api.profiles;

import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is used to cache profiles.
 * <p>
 * It implements a simple cache that stores the profile's UUIDs as value in a map.
 * It uses T as key.
 * The interface runs via profiles.
 *
 * @param <T> the type of the object to be cached
 */
public class ProfileValueMap<T> implements Map<T, Profile> {

    /**
     * The map used to store the profile's UUIDs.
     */
    private final Map<T, UUID> profileMap;
    /**
     * The server instance.
     */
    private final Server server;
    /**
     * Type of the object to be cached.
     */
    private final Class<T> type;

    /**
     * Creates a new profile cache.
     *
     * @param server the server instance
     * @param clazz  the Class T of the object to be cached
     */
    public ProfileValueMap(final Server server, final Class<T> clazz) {
        this.profileMap = new HashMap<>();
        this.type = clazz;
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
        if (type.isInstance(key)) {
            return profileMap.containsKey(key);
        }
        return false;
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
        if (type.isInstance(key)) {
            return PlayerConverter.getID(server.getPlayer(profileMap.get(key)));
        }
        return null;
    }

    @Nullable
    @Override
    public Profile put(final T key, final Profile value) {
        profileMap.put(key, value.getProfileUUID());
        return value;
    }

    @Override
    public Profile remove(final Object key) {
        if (type.isInstance(key)) {
            return PlayerConverter.getID(server.getPlayer(profileMap.remove(key)));
        }
        return null;
    }

    @Override
    public void putAll(@NotNull final Map<? extends T, ? extends Profile> map) {
        final Map<T, UUID> entries = map.entrySet().stream()
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
    public Collection<Profile> values() {
        return profileMap.values().stream()
                .map(uuid -> PlayerConverter.getID(server.getPlayer(uuid)))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Set<Entry<T, Profile>> entrySet() {
        return profileMap.entrySet().stream()
                .map(entry -> new Entry<T, Profile>() {
                    @Override
                    public T getKey() {
                        return entry.getKey();
                    }

                    @Override
                    public Profile getValue() {
                        return PlayerConverter.getID(server.getPlayer(entry.getValue()));
                    }

                    @Override
                    public Profile setValue(final Profile value) {
                        entry.setValue(value.getProfileUUID());
                        return value;
                    }
                })
                .collect(Collectors.toSet());
    }
}
