package org.betonquest.betonquest.api.profiles;

import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is used to map profiles as keys.
 * It saves the UUID of the profile instead of the profile itself and converts it to
 * profiles on return. It also takes profiles as input
 *
 * @param <V> the type of the object to be cached
 */
public class ProfileKeyMap<V> extends AbstractMap<Profile, V> implements Map<Profile, V> {

    /**
     * The map used to store the profile's UUIDs.
     */
    private final Map<UUID, V> profileMap;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * Creates a new profile key mapping.
     *
     * @param server the server instance
     */
    public ProfileKeyMap(final Server server) {
        this(new HashMap<>(), server);
    }

    public ProfileKeyMap(final Map<UUID, V> profileMap, final Server server) {
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
        if (key instanceof Profile profile) {
            return profileMap.containsKey(profile.getProfileUUID());
        }
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        return profileMap.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        if (key instanceof Profile profile) {
            return profileMap.get(profile.getProfileUUID());
        }
        return null;
    }

    @Nullable
    @Override
    public V put(final Profile key, final V value) {
        return profileMap.put(key.getProfileUUID(), value);
    }

    @Override
    public V remove(final Object key) {
        if (key instanceof Profile profile) {
            return profileMap.remove(profile.getProfileUUID());
        }
        return null;
    }

    @Override
    public void putAll(@NotNull final Map<? extends Profile, ? extends V> map) {
        final Map<UUID, ? extends V> entries = map.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getProfileUUID(), Entry::getValue));
        profileMap.putAll(entries);
    }

    @Override
    public void clear() {
        profileMap.clear();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return profileMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<Profile, V>> entrySet() {
        final Set<Entry<UUID, V>> entries = profileMap.entrySet();
        return new AbstractSet<>() {
            @Override
            public int size() {
                return entries.size();
            }

            @Override
            public boolean isEmpty() {
                return entries.isEmpty();
            }

            @Override
            public boolean contains(final Object o) {
                if (o instanceof Entry<?, ?> objectEntry && objectEntry.getKey() instanceof Profile profile) {
                    final UUID key = profile.getProfileUUID();
                    final Object value = objectEntry.getValue();
                    for (final Entry<UUID, V> entry : entries) {
                        if (Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), value)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @NotNull
            @Override
            public Iterator<Entry<Profile, V>> iterator() {
                final Iterator<Entry<UUID, V>> iterator = entries.iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<Profile, V> next() {
                        return new ProfileEntry<>(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean remove(final Object o) {
                if (o instanceof Entry<?, ?> objectEntry && objectEntry.getKey() instanceof Profile) {
                    return entries.remove(new UUIDEntry<>((Entry<Profile, V>) objectEntry));
                }
                return false;
            }

            @Override
            public void clear() {
                entries.clear();
            }
        };
    }

    private class ProfileEntry<V> implements Entry<Profile, V> {

        private final Entry<UUID, V> entry;

        public ProfileEntry(final Entry<UUID, V> entry) {
            this.entry = entry;
        }

        @Override
        public Profile getKey() {
            return PlayerConverter.getID(server.getPlayer(entry.getKey()));
        }

        @Override
        public V getValue() {
            return entry.getValue();
        }

        @Override
        public V setValue(final V value) {
            return entry.setValue(value);
        }
    }

    private class UUIDEntry<V> implements Entry<UUID, V> {

        private final UUID key;
        private V value;

        public UUIDEntry(final UUID uuid, final V value) {
            this.key = uuid;
            this.value = value;
        }

        public UUIDEntry(final Entry<Profile, V> entry) {
            this.key = entry.getKey().getProfileUUID();
            this.value = entry.getValue();
        }

        @Override
        public UUID getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Entry<?, ?> other) {
                return (this.getKey() == null ?
                        other.getKey() == null : this.getKey().equals(other.getKey())) &&
                        (this.getValue() == null ?
                                other.getValue() == null : this.getValue().equals(other.getValue()));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^
                    (this.getValue() == null ? 0 : this.getValue().hashCode());
        }
    }
}
