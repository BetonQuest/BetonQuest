package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleConfigurationOptions;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationSectionModificationHandler.getAbsolutePath;

/**
 * This {@link MultiConfiguration} merges multiple {@link ConfigurationSection} to one big Configuration.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class MultiSectionConfiguration extends HandleModificationConfiguration implements MultiConfiguration {
    /**
     * All keys and a list of files that contains them.
     */
    private final Map<String, List<ConfigurationSection>> keyIndex;

    /**
     * The configs, that where modified, and need a save.
     */
    private final Set<ConfigurationSection> unsavedConfigs;

    /**
     * Creates a new {@link MultiSectionConfiguration} from a set of source {@link ConfigurationSection}s.
     * All {@link ConfigurationSection}s are merged together.
     * It is still possible to set values.
     * If the values exist in a source config, it will set them there,
     * otherwise it is saved in a separate {@link ConfigurationSection}.
     *
     * @param sourceConfigs All configs to merge
     * @throws KeyConflictException             if two or more configs define conflicting entries
     * @throws InvalidSubConfigurationException if a source configuration is invalid in some way
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public MultiSectionConfiguration(final List<? extends ConfigurationSection> sourceConfigs) throws KeyConflictException, InvalidSubConfigurationException {
        super(new MemoryConfiguration(), new MultiConfigurationHandler());
        checkSourceConfigs(sourceConfigs);
        ((MultiConfigurationHandler) handler).setConsumer(getSetConsumer());
        this.unsavedConfigs = new CopyOnWriteArraySet<>();
        this.keyIndex = Collections.synchronizedMap(new LinkedHashMap<>());

        buildKeyIndex(sourceConfigs);
        validateKeyIndex();
        mergeFromKeyIndex();
        final List<ConfigurationSection> defaultList = sourceConfigs.stream().map(ConfigurationSection::getDefaultSection).filter(Objects::nonNull).toList();
        if (!defaultList.isEmpty()) {
            original.setDefaults(new MultiSectionConfiguration(defaultList));
        }
    }

    private void checkSourceConfigs(final List<? extends ConfigurationSection> sourceConfigs) throws InvalidSubConfigurationException {
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
            public void set(final String path, @Nullable final Object value) {
                checkConflictAndSet(path, value);
            }

            @Override
            public void setComment(final String path, @Nullable final List<String> comments) {
                if (keyIndex.containsKey(path)) {
                    final ConfigurationSection config = keyIndex.get(path).get(0);
                    if (config != null && config.isSet(path)) {
                        config.setComments(getReplacedPath(path, config), comments);
                        unsavedConfigs.add(config);
                    }
                }
                original.setComments(path, comments);
            }

            @Override
            public void setInlineComment(final String path, @Nullable final List<String> comments) {
                if (keyIndex.containsKey(path)) {
                    final ConfigurationSection config = keyIndex.get(path).get(0);
                    if (config != null && config.isSet(path)) {
                        config.setInlineComments(getReplacedPath(path, config), comments);
                        unsavedConfigs.add(config);
                    }
                }
                original.setInlineComments(path, comments);
            }
        };
    }

    /**
     * Extracts all keys from all sections and puts them into a key to section-list map.
     *
     * @param sourceConfigs the configs that should represent this config
     */
    private void buildKeyIndex(final List<? extends ConfigurationSection> sourceConfigs) {
        sourceConfigs.forEach(sourceConfig -> sourceConfig.getKeys(true).stream()
                .filter(sectionKey -> !sourceConfig.isConfigurationSection(sectionKey))
                .forEach(sectionKey -> addToList(sectionKey, sourceConfig)));
    }

    /**
     * Only validate if the given configurations can be merged. This method does not merge the configs!
     *
     * @throws KeyConflictException when the given section contains conflicting keys
     */
    private void validateKeyIndex() throws KeyConflictException {
        final Map<String, List<ConfigurationSection>> conflictingKeys = validateKeyIndexKeys();
        final List<List<Pair<String, ConfigurationSection>>> conflictingPaths = validateKeyIndexPaths();

        if (!conflictingKeys.isEmpty() || !conflictingPaths.isEmpty()) {
            throw new KeyConflictException(conflictingKeys, conflictingPaths);
        }
    }

    private Map<String, List<ConfigurationSection>> validateKeyIndexKeys() {
        return keyIndex.entrySet().parallelStream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("NullAway")
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

    @SuppressWarnings("NullAway")
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

    private void addToList(final String sectionKey, final ConfigurationSection sourceConfig) {
        keyIndex.computeIfAbsent(sectionKey, key -> new CopyOnWriteArrayList<>()).add(sourceConfig);
    }

    @Override
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

    @Override
    public Set<ConfigurationSection> getUnsavedConfigs() {
        return new HashSet<>(unsavedConfigs);
    }

    @Override
    public boolean markAsSaved(final ConfigurationSection section) {
        return unsavedConfigs.remove(section);
    }

    @Override
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Nullable
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

    @Override
    public List<String> getUnassociatedKeys() {
        final List<String> unassociatedKeys = new ArrayList<>();
        for (final String key : original.getKeys(true)) {
            if (!original.isConfigurationSection(key) && !keyIndex.containsKey(key) && original.isSet(key)) {
                unassociatedKeys.add(key);
            }
        }
        return unassociatedKeys;
    }

    @Override
    public void associateWith(final ConfigurationSection targetConfig) {
        for (final String key : getUnassociatedKeys()) {
            associateWith(key, targetConfig);
        }
    }

    @Override
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
                associatedConfig.set(getReplacedPath(path, associatedConfig), null);
                unsavedConfigs.add(associatedConfig);
            }
            keyIndex.get(path).set(0, targetConfig);
        } else {
            addToList(path, targetConfig);
        }
        final String replacedPath = getReplacedPath(path, targetConfig);
        targetConfig.set(replacedPath, original.get(path));
        targetConfig.setComments(replacedPath, original.getComments(path));
        targetConfig.setInlineComments(replacedPath, original.getInlineComments(path));
        unsavedConfigs.add(targetConfig);
    }

    private void checkConflictAndSet(final String path, @Nullable final Object value) {
        if (value == null || value instanceof ConfigurationSection) {
            deletePath(path);
            if (value == null) {
                return;
            }
            final ConfigurationSection config = (ConfigurationSection) value;
            final ConfigurationSection targetSection = createSection(path);
            for (final String key : config.getKeys(false)) {
                checkConflictAndSet(getAbsolutePath(targetSection, key), config.get(key));
            }
            return;
        }

        if (keyIndex.containsKey(path)) {
            final ConfigurationSection config = keyIndex.get(path).get(0);
            if (config != null && config.isSet(path)) {
                config.set(getReplacedPath(path, config), value);
                unsavedConfigs.add(config);
            }
        }
        original.set(path, value);
    }

    private void deletePath(final String path) {
        original.set(path, null);
        for (final Map.Entry<String, List<ConfigurationSection>> entry : keyIndex.entrySet()) {
            final ConfigurationSection config = entry.getValue().get(0);
            if (config != null && config.isSet(path)) {
                config.set(getReplacedPath(path, config), null);
                unsavedConfigs.add(config);
            }
        }
    }

    private String getReplacedPath(final String path, final ConfigurationSection config) {
        final Configuration root = config.getRoot();
        if (root == null) {
            throw new IllegalStateException("One source config does not have a root!");
        }
        return path.replaceAll(Pattern.quote(String.valueOf(options().pathSeparator())), String.valueOf(root.options().pathSeparator()));
    }

    @Override
    public ConfigurationOptions options() {
        return new MultiConfigurationOptions(this, original.options());
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
        void set(String path, @Nullable Object value);

        /**
         * Consumer for a call of the setComment method.
         *
         * @param path     the absolut path to the comments
         * @param comments the comments to set
         */
        void setComment(String path, @Nullable List<String> comments);

        /**
         * Consumer for a call of the setInlineComment method.
         *
         * @param path     the absolut path to the comments
         * @param comments the comments to set
         */
        void setInlineComment(String path, @Nullable List<String> comments);
    }

    /**
     * A {@link ConfigurationModificationHandler} for a {@link MultiSectionConfiguration}.
     */
    private static class MultiConfigurationHandler implements ConfigurationModificationHandler {
        /**
         * The consumer to call if the set method was called.
         */
        @Nullable
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
        public void set(final ConfigurationSection section, final String path, @Nullable final Object value) {
            if (consumer != null) {
                consumer.set(getAbsolutePath(section, path), value);
            }
        }

        @Override
        public ConfigurationSection createSection(final ConfigurationSection section, final String path) {
            return new HandleModificationConfigurationSection(section.createSection(path), this);
        }

        @Override
        public ConfigurationSection createSection(final ConfigurationSection section, final String path, final Map<?, ?> map) {
            return new HandleModificationConfigurationSection(section.createSection(path, map), this);
        }

        @Override
        public void setComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
            if (consumer != null) {
                consumer.setComment(getAbsolutePath(section, path), comments);
            }
        }

        @Override
        public void setInlineComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
            if (consumer != null) {
                consumer.setInlineComment(getAbsolutePath(section, path), comments);
            }
        }

        @Override
        public void addDefault(final ConfigurationSection section, final String path, @Nullable final Object value) {
            section.addDefault(path, value);
        }

        @Override
        public void addDefaults(final Configuration section, final Map<String, Object> defaults) {
            section.addDefaults(defaults);
        }

        @Override
        public void addDefaults(final Configuration section, final Configuration defaults) {
            section.addDefaults(defaults);
        }

        @Override
        public void setDefaults(final Configuration section, final Configuration defaults) {
            section.setDefaults(defaults);
        }
    }

    /**
     * {@link ConfigurationOptions} for a {@link MultiSectionConfiguration},
     * that converts all entries in the {@link MultiConfigurationOptions#keyIndex}.
     */
    private class MultiConfigurationOptions extends HandleConfigurationOptions {

        /**
         * Creates a new {@link ConfigurationOptions} instance, that maps to the original one.
         *
         * @param configuration The {@link Configuration} instance that should be returned by the configuration method
         * @param original      The original {@link Configuration}, to apply the options to
         */
        protected MultiConfigurationOptions(final Configuration configuration, final ConfigurationOptions original) {
            super(configuration, original);
        }

        @Override
        public ConfigurationOptions pathSeparator(final char value) {
            final Map<String, List<ConfigurationSection>> newKeyIndex = new ConcurrentHashMap<>();
            keyIndex.forEach((key, mapValue) -> newKeyIndex.put(key.replace(options().pathSeparator(), value), mapValue));

            keyIndex.clear();
            keyIndex.putAll(newKeyIndex);

            return super.pathSeparator(value);
        }
    }
}
