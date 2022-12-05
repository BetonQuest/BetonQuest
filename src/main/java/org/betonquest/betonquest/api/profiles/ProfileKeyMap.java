package org.betonquest.betonquest.api.profiles;

import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
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
        return new Set<>() {
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
                        return getEntryFromUUID(iterator.next());
                    }
                };
            }

            @NotNull
            @Override
            public Object[] toArray() {
                final Object[] objects = entries.toArray();
                final Object[] convertedObjects = new Object[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    convertedObjects[i] = getEntryFromUUID((Entry<UUID, V>) objects[i]);
                }
                return convertedObjects;
            }

            @NotNull
            @Override
            public <T> T[] toArray(@NotNull final T[] a) {
                final T[] objects = entries.toArray(a);
                final T[] convertedObjects = new T[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    convertedObjects[i] = getEntryFromUUID((Entry<UUID, V>) objects[i]);
                }
                return convertedObjects;
            }

            @Override
            public boolean add(final Entry<Profile, V> profileTEntry) {
                return entries.add(getEntryFromProfile(profileTEntry));
            }

            @Override
            public boolean remove(final Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NotNull final Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NotNull final Collection<? extends Entry<Profile, V>> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NotNull final Collection<?> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NotNull final Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {
                entries.clear();
            }

            @NotNull
            private Entry<Profile, V> getEntryFromUUID(final Entry<UUID, V> entry) {
                return new Entry<>() {
                    @Override
                    public Profile getKey() {
                        return PlayerConverter.getID(server.getOfflinePlayer(entry.getKey()));
                    }

                    @Override
                    public V getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public V setValue(final V value) {
                        return entry.setValue(value);
                    }
                };
            }

            @NotNull
            private Entry<UUID, V> getEntryFromProfile(final Entry<Profile, V> entry) {
                return new Entry<>() {
                    @Override
                    public UUID getKey() {
                        return entry.getKey().getProfileUUID();
                    }

                    @Override
                    public V getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public V setValue(final V value) {
                        return entry.setValue(value);
                    }
                };
            }
        };
    }
}
