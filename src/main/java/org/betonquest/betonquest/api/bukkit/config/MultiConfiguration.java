package org.betonquest.betonquest.api.bukkit.config;

import org.betonquest.betonquest.api.bukkit.config.custom.DelegateConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.DelegateConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.DelegateModificationConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.UnmodifiableConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class MultiConfiguration extends MemoryConfiguration implements ConfigurationSection {

    private final ConfigurationSection[] sourceConfigs;

    private final ConfigurationSection unassociatedKeys;

    private final Set<ConfigurationSection> unsavedConfigs;

    private final Map<String, List<ConfigurationSection>> keyList;

    private final MultiConfigurationSetter setter;

    public MultiConfiguration(final ConfigurationSection... configs) throws KeyConflictConfigurationException {
        super();
        this.sourceConfigs = configs;
        this.unassociatedKeys = new MemoryConfiguration();
        this.unsavedConfigs = new CopyOnWriteArraySet<>();
        this.keyList = new ConcurrentHashMap<>();
        loadKeyList();
        validateMerge();
        merge();
        this.setter = new MultiConfigurationSetter();
    }

    /**
     * Only validate if the given configurations can be merged. This method does not merge the configs!
     *
     * @throws KeyConflictConfigurationException when the given section contain conflicting keys
     */
    private void validateMerge() throws KeyConflictConfigurationException {
        final Map<String, List<ConfigurationSection>> duplicates = findDuplicateKeys(keyList);
        if (!duplicates.isEmpty()) {
            throw new KeyConflictConfigurationException(duplicates);
        }
    }

    /**
     * Extract all keys from all section and put them into a key to section-list map.
     *
     * @return key to section-list map of given configs
     */
    private void loadKeyList() {
        Arrays.stream(sourceConfigs).forEach(sourceConfig -> sourceConfig.getKeys(true).stream()
                .filter(sectionKey -> !sourceConfig.isConfigurationSection(sectionKey))
                .forEach(sectionKey -> addToList(keyList, sectionKey, sourceConfig)));
    }

    /**
     * Analyzes key to section map for duplicated keys. It will also check for key/section collisions.
     *
     * @param keyList key to section-list map
     * @return map of duplicates
     */
    @NotNull
    private Map<String, List<ConfigurationSection>> findDuplicateKeys(final Map<String, List<ConfigurationSection>> keyList) {
        final Map<String, List<ConfigurationSection>> duplicates = keyList.entrySet().stream()
                .filter(key -> key.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        keyList.keySet().forEach(sectionKey -> Arrays.stream(sourceConfigs)
                .filter(sourceConfig -> sourceConfig.isConfigurationSection(sectionKey))
                .forEach(sourceConfig -> addToList(duplicates, sectionKey, sourceConfig)));
        return duplicates;
    }

    /**
     * Adds a value to a list inside a map. If the List doesn't exist yet it will be created.
     *
     * @param keyList      map containing lists with values
     * @param sectionKey   key to use for map
     * @param sourceConfig config to add to list
     */
    private void addToList(final Map<String, List<ConfigurationSection>> keyList, final String sectionKey, final ConfigurationSection sourceConfig) {
        keyList.computeIfAbsent(sectionKey, _key -> new CopyOnWriteArrayList<>()).add(sourceConfig);
    }

    private void merge() {
        keyList.forEach((key, value) -> super.set(key, value.get(0).get(key)));
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return new DelegateConfiguration(super.getRoot(), setter);
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return new DelegateConfigurationSection(super.getParent(), setter);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return new DelegateConfigurationSection(this, setter).get(path);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return new DelegateConfigurationSection(this, setter).get(path, def);
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        new DelegateConfigurationSection(this, setter).set(path, value);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        return new DelegateConfigurationSection(this, setter).createSection(path);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        return new DelegateConfigurationSection(this, setter).createSection(path, map);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        return new DelegateConfigurationSection(this, setter).getObject(path, clazz);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return new DelegateConfigurationSection(this, setter).getObject(path, clazz, def);
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return new DelegateConfigurationSection(this, setter).getConfigurationSection(path);
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return new DelegateConfigurationSection(this, setter).getDefaultSection();
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        new DelegateConfigurationSection(this, setter).addDefault(path, value);
    }

    private void checkAndSet(final @NotNull String path, final @Nullable Object value) {
        checkDuplicateKeys(path);

        if (keyList.containsKey(path)) {
            final ConfigurationSection config = keyList.get(path).get(0);
            config.set(path, value);
            if (config != unassociatedKeys) {
                unsavedConfigs.add(config);
            }
        } else {
            addToList(keyList, path, unassociatedKeys);
            unassociatedKeys.set(path, value);
        }
    }

    private void checkDuplicateKeys(final @NotNull String path) {
        final Map<String, List<ConfigurationSection>> duplicates = new HashMap<>();
        Arrays.stream(sourceConfigs)
                .filter(config -> config.isConfigurationSection(path))
                .forEach(config -> addToList(duplicates, path, config));
        if (!duplicates.isEmpty()) {
            throw new UncheckedKeyConflictConfigurationException(new KeyConflictConfigurationException(duplicates));
        }
    }

    public Set<ConfigurationSection> getUnsavedConfigs() {
        return unsavedConfigs.stream().map(UnmodifiableConfigurationSection::new).collect(Collectors.toSet());
    }

    public ConfigurationSection getUnassociatedKeys() {
        return new UnmodifiableConfigurationSection(unassociatedKeys);
    }

    private class MultiConfigurationSetter implements DelegateModificationConfiguration {
        @Override
        public void set(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
            final String sectionPath = (section.getCurrentPath() == null || section.getCurrentPath().isEmpty() ? "" : section.getCurrentPath() + ".") + path;
            checkAndSet(sectionPath, value);
            section.set(path, value);
        }

        @Override
        public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
            return new DelegateConfigurationSection(MultiConfiguration.super.createSection(section.getCurrentPath() + "." + path), this);
        }

        @Override
        public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map) {
            return new DelegateConfigurationSection(MultiConfiguration.super.createSection(section.getCurrentPath() + "." + path, map), this);
        }

        @Override
        public void addDefault(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {

        }

        @Override
        public void addDefaults(@NotNull final Configuration section, @NotNull final Map<String, Object> defaults) {

        }

        @Override
        public void addDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {

        }

        @Override
        public void setDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {

        }
    }
}
