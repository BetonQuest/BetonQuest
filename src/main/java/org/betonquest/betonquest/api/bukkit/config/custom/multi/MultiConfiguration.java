package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationSectionModificationHandler.getAbsolutePath;

/**
 * This {@link MultiConfiguration} merges multiple {@link ConfigurationSection} to one big Configuration.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class MultiConfiguration extends HandleModificationConfiguration {

    /**
     * Exception message for unmodifiable behaviours.
     */
    public static final String UNMODIFIABLE_MESSAGE = "The defaults of this config can not be modified";
    /**
     * All keys and a list of files that contains them.
     */
    private final Map<String, List<ConfigurationSection>> keyIndex;
    /**
     * The configs, that where modified, and need a save.
     */
    private final Set<ConfigurationSection> unsavedConfigs;

    /**
     * Creates a new {@link MultiConfiguration} from a set of source {@link ConfigurationSection}s.
     * All {@link ConfigurationSection}s are merged together.
     * It is still possible to set values.
     * If the values exist in a source config, it will set them there,
     * otherwise it is saved in a separate {@link ConfigurationSection}.
     *
     * @param sourceConfigs All configs to merge
     * @throws KeyConflictException             is thrown, if two or more configs define conflicting entries
     * @throws InvalidSubConfigurationException is thrown, if a source configuration is invalid in some way
     */
    public MultiConfiguration(final ConfigurationSection... sourceConfigs) throws KeyConflictException, InvalidSubConfigurationException {
        super(new MemoryConfiguration(), new MultiConfigurationHandler());
        checkSourceConfigs(sourceConfigs);
        ((MultiConfigurationHandler) handler).setConsumer(getSetConsumer());
        this.unsavedConfigs = new CopyOnWriteArraySet<>();
        this.keyIndex = new ConcurrentHashMap<>();

        buildKeyIndex(sourceConfigs);
        validateKeyIndex();
        mergeFromKeyIndex();
        original.options().copyDefaults(true);
    }

    private void checkSourceConfigs(final ConfigurationSection... sourceConfigs) throws InvalidSubConfigurationException {
        for (final ConfigurationSection sourceConfig : sourceConfigs) {
            final Configuration root = sourceConfig.getRoot();
            if (root == null) {
                throw new InvalidSubConfigurationException("At least one source config does not have a root!", sourceConfig);
            }
            if (root.options().pathSeparator() != options().pathSeparator()) {
                throw new InvalidSubConfigurationException("At least one source config does not have valid path separator!", sourceConfig);
            }
        }
    }

    private SetConsumer getSetConsumer() {
        return new SetConsumer() {
            @Override
            public void set(@NotNull final String path, @Nullable final Object value) {
                checkConflictAndSet(path, value);
            }

            @Override
            public void setComment(@NotNull final String path, final List<String> comments) {
                if (keyIndex.containsKey(path)) {
                    final ConfigurationSection config = keyIndex.get(path).get(0);
                    if (config != null && config.isSet(path)) {
                        config.setComments(path, comments);
                        unsavedConfigs.add(config);
                    }
                }
            }

            @Override
            public void setInlineComment(@NotNull final String path, final List<String> comments) {
                if (keyIndex.containsKey(path)) {
                    final ConfigurationSection config = keyIndex.get(path).get(0);
                    if (config != null && config.isSet(path)) {
                        config.setInlineComments(path, comments);
                        unsavedConfigs.add(config);
                    }
                }
            }
        };
    }

    /**
     * Applies all templates to this {@link MultiConfiguration}.
     *
     * @param templates all templates, the weakest first
     */
    public void setMultiDefaults(final ConfigurationSection... templates) {
        for (final ConfigurationSection template : templates) {
            for (final String key : template.getKeys(true)) {
                if (!template.isConfigurationSection(key)) {
                    original.addDefault(key, template.get(key));
                    if (!keyIndex.containsKey(key)) {
                        addToList(keyIndex, key, null);
                    }
                }
            }
        }
    }

    /**
     * Extracts all keys from all sections and puts them into a key to section-list map.
     *
     * @param sourceConfigs the configs that should represent this config
     */
    private void buildKeyIndex(final ConfigurationSection... sourceConfigs) {
        Arrays.stream(sourceConfigs).forEach(sourceConfig -> sourceConfig.getKeys(true).stream()
                .filter(sectionKey -> !sourceConfig.isConfigurationSection(sectionKey))
                .forEach(sectionKey -> addToList(keyIndex, sectionKey, sourceConfig)));
    }

    /**
     * Only validate if the given configurations can be merged. This method does not merge the configs!
     *
     * @throws KeyConflictException when the given section contain conflicting keys
     */
    private void validateKeyIndex() throws KeyConflictException {
        final Map<String, List<ConfigurationSection>> conflictingKeys = validateKeyIndexKeys();
        final List<List<Pair<String, ConfigurationSection>>> conflictingPaths = validateKeyIndexPaths();

        if (!conflictingKeys.isEmpty() || !conflictingPaths.isEmpty()) {
            throw new KeyConflictException(conflictingKeys, conflictingPaths);
        }
    }

    @NotNull
    private Map<String, List<ConfigurationSection>> validateKeyIndexKeys() {
        return keyIndex.entrySet().parallelStream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    private List<List<Pair<String, ConfigurationSection>>> validateKeyIndexPaths() {
        final List<List<Pair<String, ConfigurationSection>>> conflictingPaths = new ArrayList<>();
        final SortedSet<String> index = new TreeSet<>(keyIndex.keySet());
        while (!index.isEmpty()) {
            final String currentKey = index.first();
            index.remove(currentKey);
            final List<Pair<String, ConfigurationSection>> conflict = validateKeyIndexPathsConflicts(index, currentKey);
            if (!conflict.isEmpty()) {
                for (final ConfigurationSection section : keyIndex.get(currentKey)) {
                    conflict.add(0, Pair.of(currentKey, section));
                }
                conflictingPaths.add(conflict);
            }
        }
        return conflictingPaths;
    }

    @NotNull
    private List<Pair<String, ConfigurationSection>> validateKeyIndexPathsConflicts(final SortedSet<String> index, final String currentKey) {
        final List<Pair<String, ConfigurationSection>> conflict = new ArrayList<>();
        for (final String targetKey : index) {
            if (targetKey.startsWith(currentKey + options().pathSeparator())) {
                for (final ConfigurationSection section : keyIndex.get(targetKey)) {
                    conflict.add(Pair.of(targetKey, section));
                }
            }
        }
        for (final Pair<String, ConfigurationSection> entry : conflict) {
            index.remove(entry.getKey());
        }
        return conflict;
    }

    private void mergeFromKeyIndex() {
        keyIndex.forEach((key, value) -> {
            final ConfigurationSection config = value.get(0);
            original.set(key, config.get(key));
            original.setComments(key, config.getComments(key));
            original.setInlineComments(key, config.getInlineComments(key));
        });
    }

    private void addToList(final Map<String, List<ConfigurationSection>> keyList, final String sectionKey, final ConfigurationSection sourceConfig) {
        keyList.computeIfAbsent(sectionKey, key -> new CopyOnWriteArrayList<>()).add(sourceConfig);
    }

    /**
     * Returns if a save is needed on a {@link ConfigurationSection} or an unassociated entry.
     *
     * @return true, if a save is needed
     */
    public boolean needSave() {
        if (!unsavedConfigs.isEmpty()) {
            return true;
        }
        for (final String key : original.getKeys(true)) {
            if (!original.isConfigurationSection(key) && !keyIndex.containsKey(key) && original.isSet(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves all {@link ConfigurationSection} by calling the {@link Saver}.
     *
     * @param saver the saver to call
     */
    public void saveConfigs(final Saver saver) {
        for (final ConfigurationSection unsavedConfig : unsavedConfigs) {
            saver.save(unsavedConfig);
        }
        unsavedConfigs.clear();
    }

    /**
     * Gets the configuration of a specified path. The path can also be a configuration section.
     * <p>
     * If the path is not set in this {@link MultiConfiguration} this will return null.
     * This is also the case for default values.
     * <p>
     * If the path is a configuration section it will be checked,
     * that every entry in the configuration section is from the same source configuration section.
     * Otherwise, an {@link InvalidConfigurationException} is thrown.
     *
     * @param path The path of the entry to get the {@link ConfigurationSection} to
     * @return The clearly {@link ConfigurationSection} of the given path
     * @throws InvalidConfigurationException is thrown, if the given path is defined in multiple configuration
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ConfigurationSection getSourceConfigurationSection(final String path) throws InvalidConfigurationException {
        if (!original.isSet(path)) {
            return null;
        }
        final ConfigurationSection config = original.getConfigurationSection(path);
        if (config == null) {
            final List<ConfigurationSection> keyIndexEntry = keyIndex.get(path);
            return keyIndexEntry == null ? null : keyIndexEntry.get(0);
        }
        final Set<ConfigurationSection> configurationSections = new HashSet<>();
        for (final String key : config.getKeys(false)) {
            final String absolutePath = getAbsolutePath(config, key);
            if (original.isSet(absolutePath)) {
                configurationSections.add(getSourceConfigurationSection(absolutePath));
            }
        }
        if (configurationSections.size() > 1) {
            throw new InvalidConfigurationException("Not all entries are from the same source config");
        }
        return configurationSections.iterator().next();
    }

    /**
     * Gets all keys, that are not associated with a {@link ConfigurationSection}.
     *
     * @return a list of unassociated keys.
     */
    public List<String> getUnassociatedKeys() {
        final List<String> unassociatedKeys = new ArrayList<>();
        for (final String key : original.getKeys(true)) {
            if (!original.isConfigurationSection(key) && !keyIndex.containsKey(key) && original.isSet(key)) {
                unassociatedKeys.add(key);
            }
        }
        return unassociatedKeys;
    }

    /**
     * All entries that are not associated with a {@link ConfigurationSection}
     * will be associated with the given config.
     *
     * @param targetConfig the config to associate entries to
     */
    public void associateWith(final ConfigurationSection targetConfig) {
        for (final String key : getUnassociatedKeys()) {
            associateWith(key, targetConfig);
        }
    }

    /**
     * All entries under the given path that are not associated with a {@link ConfigurationSection}
     * will be associated with the given config.
     *
     * @param path         the path that should be associated with the given config
     * @param targetConfig the config to associate entries to
     */
    public void associateWith(final String path, final ConfigurationSection targetConfig) {
        if (!original.isSet(path)) {
            return;
        }
        final ConfigurationSection config = original.getConfigurationSection(path);
        if (config != null && original.isSet(path)) {
            for (final String key : config.getKeys(false)) {
                associateWith(getAbsolutePath(config, key), targetConfig);
            }
            return;
        }
        if (keyIndex.containsKey(path)) {
            final ConfigurationSection associatedConfig = keyIndex.get(path).get(0);
            if (associatedConfig != null && associatedConfig.isSet(path)) {
                associatedConfig.set(path, null);
                unsavedConfigs.add(associatedConfig);
            }
            keyIndex.get(path).set(0, targetConfig);
        } else {
            addToList(keyIndex, path, targetConfig);
        }
        targetConfig.set(path, original.get(path));
        unsavedConfigs.add(targetConfig);
    }

    private void checkConflictAndSet(final @NotNull String path, final @Nullable Object value) {
        if (value == null || value instanceof ConfigurationSection) {
            deletePath(path);
            if (value == null) {
                return;
            }
            final ConfigurationSection config = (ConfigurationSection) value;
            for (final String key : config.getKeys(false)) {
                checkConflictAndSet(getAbsolutePath(config, key), config.get(key));
            }
            return;
        }

        if (keyIndex.containsKey(path)) {
            final ConfigurationSection config = keyIndex.get(path).get(0);
            if (config != null && config.isSet(path)) {
                config.set(path, value);
                unsavedConfigs.add(config);
            }
        }
    }

    private void deletePath(final String path) {
        original.set(path, null);
        for (final Map.Entry<String, List<ConfigurationSection>> entry : keyIndex.entrySet()) {
            final ConfigurationSection config = entry.getValue().get(0);
            if (config != null && config.isSet(path)) {
                config.set(path, null);
                unsavedConfigs.add(config);
            }
        }
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return new UnmodifiableConfigurationSection(original.getDefaultSection());
    }

    /**
     * Handles saves that need to be done on {@link ConfigurationSection} instances.
     */
    public interface Saver {
        /**
         * This method gets called for a configuration instance that needs to be saved.
         *
         * @param unsaved the instance of an unsaved {@link ConfigurationSection}
         */
        void save(ConfigurationSection unsaved);
    }

    /**
     * This class is designed to get called when the method
     * {@link MultiConfigurationHandler#set(ConfigurationSection, String, Object)}  or the method
     * {@link MultiConfigurationHandler#setComments(ConfigurationSection, String, List)} is called.
     */
    private interface SetConsumer {
        /**
         * Consumer for a call of the set method.
         *
         * @param path  the absolut path to the value
         * @param value the value to set
         */
        void set(final @NotNull String path, final @Nullable Object value);

        /**
         * Consumer for a call of the setComment method.
         *
         * @param path     the absolut path to the comments
         * @param comments the comments to set
         */
        void setComment(final @NotNull String path, final List<String> comments);

        /**
         * Consumer for a call of the setInlineComment method.
         *
         * @param path     the absolut path to the comments
         * @param comments the comments to set
         */
        void setInlineComment(final @NotNull String path, final List<String> comments);
    }

    /**
     * A {@link ConfigurationModificationHandler} for a {@link MultiConfiguration}.
     */
    private static class MultiConfigurationHandler implements ConfigurationModificationHandler {
        /**
         * The consumer to call if the set method was called
         */
        private SetConsumer consumer;

        /**
         * Creates a new handler instance.
         */
        public MultiConfigurationHandler() {
        }

        public void setConsumer(final SetConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void set(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
            if (consumer != null) {
                consumer.set(getAbsolutePath(section, path), value);
            }
            section.set(path, value);
        }

        @NotNull
        @Override
        public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
            return new HandleModificationConfigurationSection(section.createSection(path), this);
        }

        @NotNull
        @Override
        public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map) {
            return new HandleModificationConfigurationSection(section.createSection(path, map), this);
        }

        @Override
        public void setComments(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments) {
            if (consumer != null) {
                consumer.setComment(getAbsolutePath(section, path), comments);
            }
            section.setComments(path, comments);
        }

        @Override
        public void setInlineComments(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments) {
            if (consumer != null) {
                consumer.setInlineComment(getAbsolutePath(section, path), comments);
            }
            section.setInlineComments(path, comments);
        }

        @Override
        public void addDefault(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
            throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
        }

        @Override
        public void addDefaults(@NotNull final Configuration section, @NotNull final Map<String, Object> defaults) {
            throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
        }

        @Override
        public void addDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
            throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
        }

        @Override
        public void setDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
            throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
        }
    }
}
