package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.bukkit.config.custom.lazy.LazyConfigurationSection;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class hides an original and a fallback {@link ConfigurationSection} and exposes it,
 * as if it were the original {@link ConfigurationSection}, except for a missing key, then the fallback is used.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.TooManyMethods",
        "PMD.CouplingBetweenObjects"})
public class FallbackConfigurationSection implements ConfigurationSection {
    /**
     * Manager holing the original and the fallback {@link ConfigurationSection} instances.
     */
    protected final ConfigManager manager;

    /**
     * The parent {@link FallbackConfigurationSection}.
     */
    @Nullable
    protected FallbackConfigurationSection parent;

    /**
     * The root {@link Configuration}.
     */
    protected FallbackConfiguration root;

    /**
     * Creates a new fallback {@link ConfigurationSection} instance.
     *
     * @param original The original {@link ConfigurationSection} that should be used.
     * @param fallback The fallback {@link ConfigurationSection} that should be used.
     */
    public FallbackConfigurationSection(final ConfigurationSection original, @Nullable final ConfigurationSection fallback) {
        if (!(this instanceof FallbackConfiguration)) {
            throw new IllegalStateException("Cannot construct a root FallbackConfigurationSection when not a Configuration");
        }
        this.parent = null;
        this.root = (FallbackConfiguration) this;
        this.manager = new ConfigManager(null, original, fallback);
    }

    private FallbackConfigurationSection(final FallbackConfigurationSection parent, final String sectionName, final ConfigurationSection original, @Nullable final ConfigurationSection fallback) {
        this.parent = parent;
        this.root = parent.root;
        this.manager = new ConfigManager(sectionName, original, fallback);
    }

    @Nullable
    private FallbackConfigurationSection getFallbackConfigurationSection(final String path) {
        final char separator = root.options().pathSeparator();
        final int separatorIndex = path.indexOf(separator);

        if (separatorIndex != -1) {
            final String prefix = path.substring(0, separatorIndex);
            final FallbackConfigurationSection fallbackConfigurationSection = getFallbackConfigurationSection(prefix);
            if (fallbackConfigurationSection == null) {
                return null;
            }
            final String suffix = path.substring(separatorIndex + 1);
            return fallbackConfigurationSection.getFallbackConfigurationSection(suffix);
        }
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        ConfigurationSection originalConfigurationSection = original.getConfigurationSection(path);
        final ConfigurationSection fallbackConfigurationSection = fallback == null ? null : fallback.getConfigurationSection(path);

        if (originalConfigurationSection == null) {
            if (fallbackConfigurationSection == null) {
                return null;
            }
            originalConfigurationSection = new LazyConfigurationSection(original, path);
        }
        return new FallbackConfigurationSection(this, path, originalConfigurationSection, fallbackConfigurationSection);
    }

    @Override
    public Set<String> getKeys(final boolean deep) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        final Set<String> keys = new HashSet<>();
        if (fallback != null) {
            keys.addAll(fallback.getKeys(deep));
        }
        keys.addAll(original.getKeys(deep));
        return keys;
    }

    @Override
    public Map<String, Object> getValues(final boolean deep) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        final Map<String, Object> values = new LinkedHashMap<>();
        if (fallback != null) {
            values.putAll(fallback.getValues(deep));
        }
        values.putAll(original.getValues(deep));
        replaceChildConfigurationSections(values);
        return values;
    }

    private void replaceChildConfigurationSections(final Map<String, Object> values) {
        final boolean copyDefaults = root.options().copyDefaults();
        root.options().copyDefaults(false);
        values.replaceAll(this::wrapChildConfigurationSection);
        root.options().copyDefaults(copyDefaults);
    }

    @Nullable
    private Object wrapChildConfigurationSection(final String key, final Object value) {
        if (value instanceof ConfigurationSection) {
            if (isSet(key)) {
                return getFallbackConfigurationSection(key);
            } else {
                final ConfigurationSection defaultSection = getDefaultSection();
                return defaultSection == null ? null : defaultSection.getConfigurationSection(key);
            }
        }
        return value;
    }

    @Override
    public boolean contains(final String path) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original.contains(path) || fallback != null && fallback.contains(path);
    }

    @Override
    public boolean contains(final String path, final boolean ignoreDefault) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original.contains(path, ignoreDefault) || fallback != null && fallback.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(final String path) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original.isSet(path) || fallback != null && fallback.isSet(path);
    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Nullable
    public String getCurrentPath() {
        if (this == root || parent == null) {
            return "";
        }
        if (parent == root) {
            return manager.sectionName;
        }
        return parent.getCurrentPath() + root.options().pathSeparator() + manager.sectionName;
    }

    @Override
    public String getName() {
        final ConfigurationSection original = manager.getOriginal();
        return original.getName();
    }

    @Override
    @Nullable
    public Configuration getRoot() {
        return root;
    }

    @Override
    @Nullable
    public ConfigurationSection getParent() {
        return parent;
    }

    @Override
    @Nullable
    public Object get(final String path) {
        if (isConfigurationSection(path)) {
            return getFallbackConfigurationSection(path);
        }
        return getOriginalOrFallback(path, ConfigurationSection::get);
    }

    @Override
    @Nullable
    public Object get(final String path, @Nullable final Object def) {
        if (isConfigurationSection(path)) {
            return getFallbackConfigurationSection(path);
        }
        return getOriginalOrFallback(path, ConfigurationSection::get, def);
    }

    @Override
    public void set(final String path, @Nullable final Object value) {
        manager.getOriginal().set(path, value);
    }

    @Override
    public ConfigurationSection createSection(final String path) {
        final ConfigurationSection original = manager.getOriginal();
        original.createSection(path);
        final FallbackConfigurationSection configurationSection = getFallbackConfigurationSection(path);
        if (configurationSection == null) {
            throw new IllegalStateException("Cannot create a section when both the original and the fallback configuration section are null");
        }
        return configurationSection;
    }

    @Override
    public ConfigurationSection createSection(final String path, final Map<?, ?> map) {
        final ConfigurationSection original = manager.getOriginal();
        original.createSection(path, map);
        final FallbackConfigurationSection configurationSection = getFallbackConfigurationSection(path);
        if (configurationSection == null) {
            throw new IllegalStateException("Cannot create a section when both the original and the fallback configuration section are null");
        }
        return configurationSection;
    }

    @Override
    @Nullable
    public String getString(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getString);
    }

    @Override
    @Nullable
    public String getString(final String path, @Nullable final String def) {
        return getOriginalOrFallback(path, ConfigurationSection::getString, def);
    }

    @Override
    public boolean isString(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isString);
    }

    @Override
    public int getInt(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getInt);
    }

    @Override
    public int getInt(final String path, final int def) {
        return getOriginalOrFallback(path, ConfigurationSection::getInt, def);
    }

    @Override
    public boolean isInt(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isInt);
    }

    @Override
    public boolean getBoolean(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getBoolean);
    }

    @Override
    public boolean getBoolean(final String path, final boolean def) {
        return getOriginalOrFallback(path, ConfigurationSection::getBoolean, def);
    }

    @Override
    public boolean isBoolean(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isBoolean);
    }

    @Override
    public double getDouble(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getDouble);
    }

    @Override
    public double getDouble(final String path, final double def) {
        return getOriginalOrFallback(path, ConfigurationSection::getDouble, def);
    }

    @Override
    public boolean isDouble(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isDouble);
    }

    @Override
    public long getLong(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLong);
    }

    @Override
    public long getLong(final String path, final long def) {
        return getOriginalOrFallback(path, ConfigurationSection::getLong, def);
    }

    @Override
    public boolean isLong(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isLong);
    }

    @Override
    @Nullable
    public List<?> getList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getList);
    }

    @Override
    @Nullable
    public List<?> getList(final String path, @Nullable final List<?> def) {
        return getOriginalOrFallback(path, ConfigurationSection::getList, def);
    }

    @Override
    public boolean isList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isList);
    }

    @Override
    public List<String> getStringList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getStringList);
    }

    @Override
    public List<Integer> getIntegerList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getIntegerList);
    }

    @Override
    public List<Boolean> getBooleanList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getBooleanList);
    }

    @Override
    public List<Double> getDoubleList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getDoubleList);
    }

    @Override
    public List<Float> getFloatList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getFloatList);
    }

    @Override
    public List<Long> getLongList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLongList);
    }

    @Override
    public List<Byte> getByteList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getByteList);
    }

    @Override
    public List<Character> getCharacterList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getCharacterList);
    }

    @Override
    public List<Short> getShortList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getShortList);
    }

    @Override
    public List<Map<?, ?>> getMapList(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getMapList);
    }

    @Override
    @Nullable
    public <T> T getObject(final String path, final Class<T> clazz) {
        if (isConfigurationSection(path)) {
            final ConfigurationSection config = getFallbackConfigurationSection(path);
            if (config == null) {
                return null;
            }
            return clazz.isInstance(config) ? clazz.cast(config) : null;
        }
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getObject(sectionPath, clazz));
    }

    @Override
    @Contract("_, _, !null -> !null")
    @Nullable
    public <T> T getObject(final String path, final Class<T> clazz, @Nullable final T def) {
        if (isConfigurationSection(path)) {
            final ConfigurationSection config = getFallbackConfigurationSection(path);
            if (config == null) {
                return null;
            }
            return clazz.isInstance(config) ? clazz.cast(config) : def;
        }
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getObject(sectionPath, clazz, def));
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getSerializable(sectionPath, clazz));
    }

    @Override
    @Contract("_, _, !null -> !null")
    @Nullable
    public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz, @Nullable final T def) {
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getSerializable(sectionPath, clazz), def);
    }

    @Override
    @Nullable
    public Vector getVector(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getVector);
    }

    @Override
    @Nullable
    public Vector getVector(final String path, @Nullable final Vector def) {
        return getOriginalOrFallback(path, ConfigurationSection::getVector, def);
    }

    @Override
    public boolean isVector(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isVector);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getOfflinePlayer);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(final String path, @Nullable final OfflinePlayer def) {
        return getOriginalOrFallback(path, ConfigurationSection::getOfflinePlayer, def);
    }

    @Override
    public boolean isOfflinePlayer(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isOfflinePlayer);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getItemStack);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(final String path, @Nullable final ItemStack def) {
        return getOriginalOrFallback(path, ConfigurationSection::getItemStack, def);
    }

    @Override
    public boolean isItemStack(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isItemStack);
    }

    @Override
    @Nullable
    public Color getColor(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getColor);
    }

    @Override
    @Nullable
    public Color getColor(final String path, @Nullable final Color def) {
        return getOriginalOrFallback(path, ConfigurationSection::getColor, def);
    }

    @Override
    public boolean isColor(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isColor);
    }

    @Override
    @Nullable
    public Location getLocation(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLocation);
    }

    @Override
    @Nullable
    public Location getLocation(final String path, @Nullable final Location def) {
        return getOriginalOrFallback(path, ConfigurationSection::getLocation, def);
    }

    @Override
    public boolean isLocation(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isLocation);
    }

    @Override
    @Nullable
    public ConfigurationSection getConfigurationSection(final String path) {
        return getFallbackConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isConfigurationSection);
    }

    @Override
    @Nullable
    public ConfigurationSection getDefaultSection() {
        final Configuration defaults = root.getDefaults();
        if (defaults == null) {
            return null;
        }
        final String currentPath = getCurrentPath();
        if (currentPath == null) {
            return null;
        }
        if (currentPath.isEmpty()) {
            return defaults;
        }
        return defaults.getConfigurationSection(currentPath);
    }

    @Override
    public void addDefault(final String path, @Nullable final Object value) {
        manager.getOriginal().addDefault(path, value);
    }

    @Override
    public List<String> getComments(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getComments);
    }

    @Override
    public List<String> getInlineComments(final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getInlineComments);
    }

    @Override
    public void setComments(final String path, @Nullable final List<String> comments) {
        manager.getOriginal().setComments(path, comments);
    }

    @Override
    public void setInlineComments(final String path, @Nullable final List<String> comments) {
        manager.getOriginal().setInlineComments(path, comments);
    }

    /**
     * Gets the original, fallback or default value of the given path using the given function.
     *
     * @param path     The path to get the value from
     * @param function The function to use to get the value
     * @param <T>      The type of the value
     * @return The value
     */
    private <T> T getOriginalOrFallback(final String path, final ConfigurationConsumer<T> function) {
        return getOriginalOrFallbackWithDefault(path, function, (configs) -> {
            final ConfigurationSection original = configs.getLeft();
            final ConfigurationSection fallback = configs.getRight();
            if (!original.contains(path) && fallback != null && fallback.contains(path)) {
                return function.consume(fallback, path);
            }
            return function.consume(original, path);
        });
    }

    /**
     * This method tries to get a set value from the original or the fallback configuration.
     * If no value is set, the passed default value is returned instead of the default value
     * that may be present in the original or fallback.
     *
     * @param path     The path to the value
     * @param function The function to use to get the value
     * @param def      The default value to return if no value is set
     * @param <T>      The type of the value
     * @return The value or the given default value
     */
    @Contract("_, _, !null -> !null")
    @Nullable
    private <T> T getOriginalOrFallback(final String path, final ConfigurationConsumer<T> function, @Nullable final T def) {
        return getOriginalOrFallbackWithDefault(path, function, (configs) -> def);
    }

    /**
     * Tries to get the set value from the original configuration using {@link ConfigurationSection#isSet(String)}.
     * If it is not present, it tries to get it from the fallback configuration.
     * If it is not present there either, it calls the defFunction to get the default value.
     *
     * @param path        The path to the value
     * @param function    The function to get the default value
     * @param defFunction The function to get the default value
     * @param <T>         The type of the value to obtain
     * @return The value
     */
    private <T> T getOriginalOrFallbackWithDefault(final String path, final ConfigurationConsumer<T> function, final Function<Pair<ConfigurationSection, ConfigurationSection>, T> defFunction) {
        final ConfigurationSection original = manager.getOriginal();
        if (original.isSet(path)) {
            return function.consume(original, path);
        }
        final ConfigurationSection fallback = manager.getFallback();
        if (fallback != null && fallback.isSet(path)) {
            return function.consume(fallback, path);
        }
        return defFunction.apply(Pair.of(original, fallback));
    }

    @Override
    public String toString() {
        final Configuration root = this.getRoot();
        return this.getClass().getSimpleName() + "[path='" + this.getCurrentPath() + "', root='" + (root == null ? null : root.getClass().getSimpleName()) + "']";
    }

    /**
     * Interface to consume {@link ConfigurationSection}s.
     *
     * @param <T> The type to return from the {@link ConfigurationSection}
     */
    private interface ConfigurationConsumer<T> {

        /**
         * Consumes the {@link ConfigurationSection} and returns the value.
         *
         * @param section The section to consume
         * @param path    The path to get the value from
         * @return The value
         */
        T consume(ConfigurationSection section, String path);
    }

    /**
     * Holds an original and fallback {@link ConfigurationSection}.
     * <p>
     * When the original's or fallback's getter is called, the instance will be updated from the parent section before
     * returning the value. If the parent is unset, the instances will not be updated.
     */
    protected class ConfigManager {
        /**
         * Name of the current {@link ConfigurationSection}.
         */
        @Nullable
        private final String sectionName;

        /**
         * The original {@link ConfigurationSection}.
         */
        private ConfigurationSection original;

        /**
         * The fallback {@link ConfigurationSection}.
         */
        @Nullable
        private ConfigurationSection fallback;

        /**
         * Creates a new {@link ConfigManager} with the given original and fallback {@link ConfigurationSection}.
         *
         * @param sectionName The name of the current {@link ConfigurationSection}
         * @param original    The original {@link ConfigurationSection}
         * @param fallback    The fallback {@link ConfigurationSection}
         * @throws IllegalStateException If the original and fallback {@link ConfigurationSection} is null
         */
        @SuppressWarnings({"PMD.CompareObjectsWithEquals", "PMD.AvoidUncheckedExceptionsInSignatures"})
        protected ConfigManager(@Nullable final String sectionName, final ConfigurationSection original, @Nullable final ConfigurationSection fallback) throws IllegalStateException {
            this.sectionName = sectionName;
            this.original = original;
            this.fallback = fallback;
            checkValidState(original, fallback);
        }

        @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
        private void checkValidState(@Nullable final ConfigurationSection original, @Nullable final ConfigurationSection fallback) throws IllegalStateException {
            if (original == null && fallback == null) {
                throw new IllegalStateException("Cannot construct a FallbackConfigurationSection when original and fallback are null");
            }
            if (sectionName != null && !(hasValidName(original) && hasValidName(fallback))) {
                throw new IllegalStateException("Cannot construct a FallbackConfigurationSection when sectionName is not equal to the name of the original or fallback");
            }
        }

        private boolean hasValidName(@Nullable final ConfigurationSection section) {
            if (section == null) {
                return true;
            }
            final String name = section.getName();
            return name.isEmpty() || name.equals(sectionName);
        }

        /**
         * Checks if the original {@link ConfigurationSection} is up to date, updates it if necessary, and returns it.
         *
         * @return The original {@link ConfigurationSection}
         */
        protected ConfigurationSection getOriginal() {
            if (sectionName != null && !checkIsOrphaned()) {
                if (parent == null) {
                    throw new IllegalArgumentException("Cannot construct a FallbackConfigurationSection when parent is null");
                }
                final ConfigurationSection parentOriginal = parent.manager.getOriginal();
                final ConfigurationSection newOriginal = parentOriginal.getConfigurationSection(sectionName);
                original = newOriginal == null ? new LazyConfigurationSection(parentOriginal, sectionName) : newOriginal;
            }
            return original;
        }

        /**
         * Set the original {@link ConfigurationSection} to the given ConfigurationSection.
         *
         * @param original The new original ConfigurationSection
         */
        protected void setOriginal(final ConfigurationSection original) {
            this.original = original;
        }

        /**
         * Checks if the fallback {@link ConfigurationSection} is up to date, updates it if necessary, and returns it.
         *
         * @return The fallback {@link ConfigurationSection}
         */
        @Nullable
        protected ConfigurationSection getFallback() {
            if (sectionName != null) {
                if (parent == null) {
                    throw new IllegalArgumentException("Fallback section '" + sectionName + "' has no parent");
                }
                final ConfigurationSection parentFallback = parent.manager.getFallback();
                fallback = parentFallback == null ? null : parentFallback.getConfigurationSection(sectionName);
            }
            return fallback;
        }

        @SuppressWarnings("PMD.CompareObjectsWithEquals")
        private boolean checkIsOrphaned() {
            if (sectionName != null && original != original.getRoot()) {
                final ConfigurationSection parent = original.getParent();
                return parent != null && original != parent.getConfigurationSection(sectionName);
            }
            return false;
        }
    }
}
