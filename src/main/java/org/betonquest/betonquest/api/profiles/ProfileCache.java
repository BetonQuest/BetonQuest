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
 * It implements a simple cache that stores the profile's UUIDs as key in a map.
 * The interface runs via profiles.
 *
 * @param <T> the type of the object to be cached
 */
public class ProfileCache<T> implements Map<Profile, T> {

    /**
     * The map used to store the profile's UUIDs.
     */
    private final Map<UUID, T> cache;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new profile cache.
     *
     * @param server the server instance
     */
    public ProfileCache(final Server server) {
        this.cache = new HashMap<>();
        this.server = server;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        if (key instanceof Profile profile) {
            return cache.containsKey(profile.getProfileUUID());
        }
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        return cache.containsValue(value);
    }

    @Override
    public T get(final Object key) {
        if (key instanceof Profile profile) {
            return cache.get(profile.getProfileUUID());
        }
        return null;
    }

    @Nullable
    @Override
    public T put(final Profile key, final T value) {
        return cache.put(key.getProfileUUID(), value);
    }

    @Override
    public T remove(final Object key) {
        if (key instanceof Profile profile) {
            return cache.remove(profile.getProfileUUID());
        }
        return null;
    }

    @Override
    public void putAll(@NotNull final Map<? extends Profile, ? extends T> map) {
        final Map<UUID, ? extends T> entries = map.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getProfileUUID(), Entry::getValue));
        cache.putAll(entries);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @NotNull
    @Override
    public Set<Profile> keySet() {
        return cache.keySet().stream()
                .map(uuid -> PlayerConverter.getID(server.getOfflinePlayer(uuid)))
                .collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Collection<T> values() {
        return cache.values();
    }

    @NotNull
    @Override
    public Set<Entry<Profile, T>> entrySet() {
        return cache.entrySet().stream()
                .map(entry -> new Entry<Profile, T>() {
                    @Override
                    public Profile getKey() {
                        return PlayerConverter.getID(server.getOfflinePlayer(entry.getKey()));
                    }

                    @Override
                    public T getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public T setValue(final T value) {
                        return entry.setValue(value);
                    }
                })
                .collect(Collectors.toSet());
    }
}
