package org.betonquest.betonquest.lib.profile;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A map that uses a Profile as the value and a key of type K.
 *
 * @param <K> the type of the key
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProfileValueMap<K> implements Map<K, Profile> {

    /**
     * The map used to store the Keys.
     */
    private final Map<K, UUID> map;

    /**
     * The ProfileProvider used to get the Profile from the UUID.
     */
    private final ProfileProvider provider;

    /**
     * Creates a new ProfileKeyMap.
     *
     * @param provider the ProfileProvider to use
     */
    public ProfileValueMap(final ProfileProvider provider) {
        this(provider, new HashMap<>());
    }

    /**
     * Creates a new ProfileKeyMap.
     *
     * @param provider the ProfileProvider to use to get the Profile from the UUID
     * @param map      the map to use, useful to inject, for example, a ConcurrentHashMap
     */
    public ProfileValueMap(final ProfileProvider provider, final Map<K, UUID> map) {
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
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable final Object value) {
        if (value == null) {
            return map.containsKey(null);
        }
        return value instanceof final Profile profile && map.containsValue(profile.getProfileUUID());
    }

    @Nullable
    @Override
    public Profile get(@Nullable final Object key) {
        final UUID value = map.get(key);
        if (value == null) {
            return null;
        }
        return provider.getProfile(value);
    }

    @Nullable
    @Override
    public Profile put(@Nullable final K key, @Nullable final Profile value) {
        final UUID previousUuid = map.put(key, value == null ? null : value.getProfileUUID());
        return previousUuid == null ? null : provider.getProfile(previousUuid);
    }

    @Nullable
    @Override
    public Profile remove(@Nullable final Object key) {
        final UUID remove = map.remove(key);
        return remove == null ? null : provider.getProfile(remove);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends Profile> map) {
        this.map.putAll(map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() == null ? null : entry.getValue().getProfileUUID()
                )));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Profile> values() {
        return map.values().stream()
                .map(provider::getProfile)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, Profile>> entrySet() {
        return map.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), provider.getProfile(entry.getValue())))
                .collect(Collectors.toSet());
    }
}
