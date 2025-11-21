package org.betonquest.betonquest.api.profile;

import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A map that uses a Profile as the key and a value of type V.
 *
 * @param <V> the type of the value
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProfileKeyMap<V> implements Map<Profile, V> {
    /**
     * The map used to store the values.
     */
    private final Map<UUID, V> map;

    /**
     * The ProfileProvider used to get the Profile from the UUID.
     */
    private final ProfileProvider provider;

    /**
     * Creates a new ProfileKeyMap.
     *
     * @param provider the ProfileProvider to use
     */
    public ProfileKeyMap(final ProfileProvider provider) {
        this(provider, new HashMap<>());
    }

    /**
     * Creates a new ProfileKeyMap.
     *
     * @param provider the ProfileProvider to use to get the Profile from the UUID
     * @param map      the map to use, useful to inject, for example, a ConcurrentHashMap
     */
    public ProfileKeyMap(final ProfileProvider provider, final Map<UUID, V> map) {
        this.map = map;
        this.provider = provider;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable final Object key) {
        return key instanceof final Profile profile && map.containsKey(profile.getProfileUUID());
    }

    @Override
    public boolean containsValue(@Nullable final Object value) {
        return map.containsValue(value);
    }

    @Nullable
    @Override
    public V get(@Nullable final Object key) {
        if (key == null) {
            return map.get(null);
        }
        return key instanceof final Profile profile ? map.get(profile.getProfileUUID()) : null;
    }

    @Nullable
    @Override
    public V put(@Nullable final Profile key, @Nullable final V value) {
        return map.put(key == null ? null : key.getProfileUUID(), value);
    }

    @Nullable
    @Override
    public V remove(@Nullable final Object key) {
        if (key == null) {
            return map.remove(null);
        }
        return key instanceof final Profile profile ? map.remove(profile.getProfileUUID()) : null;
    }

    @Override
    public void putAll(final Map<? extends Profile, ? extends V> map) {
        this.map.putAll(map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey() == null ? null : entry.getKey().getProfileUUID(),
                        Map.Entry::getValue
                )));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Profile> keySet() {
        return map.keySet().stream()
                .map(provider::getProfile)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<Profile, V>> entrySet() {
        return map.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(provider.getProfile(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    }
}
