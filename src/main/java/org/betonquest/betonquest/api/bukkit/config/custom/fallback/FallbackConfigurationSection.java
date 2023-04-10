package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class hides an original and a fallback {@link ConfigurationSection} and exposes it,
 * as if it were the original {@link ConfigurationSection}, except for missing key, then the fallback is used.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.TooManyMethods"})
public class FallbackConfigurationSection implements ConfigurationSection {
    /**
     * Manager holing the original and the fallback {@link ConfigurationSection} instances.
     */
    protected final ConfigManager manager;

    /**
     * The parent {@link FallbackConfigurationSection}.
     */
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
    public FallbackConfigurationSection(@NotNull final ConfigurationSection original, @Nullable final ConfigurationSection fallback) {
        if (!(this instanceof FallbackConfiguration)) {
            throw new IllegalStateException("Cannot construct a root FallbackConfigurationSection when not a Configuration");
        }
        this.parent = null;
        this.root = (FallbackConfiguration) this;
        this.manager = new ConfigManager(null, original, fallback);
    }

    private FallbackConfigurationSection(@NotNull final FallbackConfigurationSection parent, @NotNull final String sectionName, @Nullable final ConfigurationSection original, @Nullable final ConfigurationSection fallback) {
        this.parent = parent;
        this.root = parent.root;
        this.manager = new ConfigManager(sectionName, original, fallback);
    }

    private FallbackConfigurationSection getFallbackConfigurationSection(final String path) {
        final char separator = root.options().pathSeparator();
        final int separatorIndex = path.indexOf(separator);

        if (separatorIndex == -1) {
            final ConfigurationSection original = manager.getOriginal();
            final ConfigurationSection fallback = manager.getFallback();
            final ConfigurationSection originalConfigurationSection = original == null ? null : original.getConfigurationSection(path);
            final ConfigurationSection fallbackConfigurationSection = fallback == null ? null : fallback.getConfigurationSection(path);

            if (originalConfigurationSection == null && fallbackConfigurationSection == null) {
                return null;
            }
            return new FallbackConfigurationSection(this, path, originalConfigurationSection, fallbackConfigurationSection);
        }

        final String prefix = path.substring(0, separatorIndex);
        final FallbackConfigurationSection fallbackConfigurationSection = getFallbackConfigurationSection(prefix);
        if (fallbackConfigurationSection == null) {
            return null;
        }
        final String suffix = path.substring(separatorIndex + 1);
        return fallbackConfigurationSection.getFallbackConfigurationSection(suffix);
    }

    @Override
    public @NotNull
    Set<String> getKeys(final boolean deep) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        final Set<String> keys = new HashSet<>();
        if (fallback != null) {
            keys.addAll(fallback.getKeys(deep));
        }
        if (original != null) {
            keys.addAll(original.getKeys(deep));
        }
        return keys;
    }

    @Override
    public @NotNull
    Map<String, Object> getValues(final boolean deep) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        final Map<String, Object> values = new LinkedHashMap<>();
        if (fallback != null) {
            values.putAll(fallback.getValues(deep));
        }
        if (original != null) {
            values.putAll(original.getValues(deep));
        }
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
    public boolean contains(@NotNull final String path) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original != null && original.contains(path) || fallback != null && fallback.contains(path);
    }

    @Override
    public boolean contains(@NotNull final String path, final boolean ignoreDefault) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original != null && original.contains(path, ignoreDefault) || fallback != null && fallback.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(@NotNull final String path) {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original != null && original.isSet(path) || fallback != null && fallback.isSet(path);
    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public @Nullable
    String getCurrentPath() {
        if (this == root) {
            return "";
        }
        if (parent == root) {
            return manager.sectionName;
        }
        return parent.getCurrentPath() + root.options().pathSeparator() + manager.sectionName;
    }

    @Override
    public @NotNull
    String getName() {
        final ConfigurationSection original = manager.getOriginal();
        final ConfigurationSection fallback = manager.getFallback();
        return original == null ? fallback.getName() : original.getName();
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return root;
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return parent;
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        if (isConfigurationSection(path)) {
            return getFallbackConfigurationSection(path);
        }
        return getOriginalOrFallback(path, ConfigurationSection::get);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        if (isConfigurationSection(path)) {
            return getFallbackConfigurationSection(path);
        }
        return getOriginalOrFallback(path, ConfigurationSection::get, def);
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        manager.getOriginal().set(path, value);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        final ConfigurationSection original = manager.getOriginal();
        if (original != null) {
            original.createSection(path);
        }
        final FallbackConfigurationSection configurationSection = getFallbackConfigurationSection(path);
        if (configurationSection == null) {
            throw new IllegalStateException("Cannot create a section when both the original and the fallback configuration section are null");
        }
        return configurationSection;
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        final ConfigurationSection original = manager.getOriginal();
        if (original != null) {
            original.createSection(path, map);
        }
        final FallbackConfigurationSection configurationSection = getFallbackConfigurationSection(path);
        if (configurationSection == null) {
            throw new IllegalStateException("Cannot create a section when both the original and the fallback configuration section are null");
        }
        return configurationSection;
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getString);
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path, @Nullable final String def) {
        return getOriginalOrFallback(path, ConfigurationSection::getString, def);
    }

    @Override
    public boolean isString(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isString);
    }

    @Override
    public int getInt(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getInt);
    }

    @Override
    public int getInt(@NotNull final String path, final int def) {
        return getOriginalOrFallback(path, ConfigurationSection::getInt, def);
    }

    @Override
    public boolean isInt(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isInt);
    }

    @Override
    public boolean getBoolean(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getBoolean);
    }

    @Override
    public boolean getBoolean(@NotNull final String path, final boolean def) {
        return getOriginalOrFallback(path, ConfigurationSection::getBoolean, def);
    }

    @Override
    public boolean isBoolean(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isBoolean);
    }

    @Override
    public double getDouble(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getDouble);
    }

    @Override
    public double getDouble(@NotNull final String path, final double def) {
        return getOriginalOrFallback(path, ConfigurationSection::getDouble, def);
    }

    @Override
    public boolean isDouble(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isDouble);
    }

    @Override
    public long getLong(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLong);
    }

    @Override
    public long getLong(@NotNull final String path, final long def) {
        return getOriginalOrFallback(path, ConfigurationSection::getLong, def);
    }

    @Override
    public boolean isLong(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isLong);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getList);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path, @Nullable final List<?> def) {
        return getOriginalOrFallback(path, ConfigurationSection::getList, def);
    }

    @Override
    public boolean isList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isList);
    }

    @Override
    public @NotNull
    List<String> getStringList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getStringList);
    }

    @Override
    public @NotNull
    List<Integer> getIntegerList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getIntegerList);
    }

    @Override
    public @NotNull
    List<Boolean> getBooleanList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getBooleanList);
    }

    @Override
    public @NotNull
    List<Double> getDoubleList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getDoubleList);
    }

    @Override
    public @NotNull
    List<Float> getFloatList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getFloatList);
    }

    @Override
    public @NotNull
    List<Long> getLongList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLongList);
    }

    @Override
    public @NotNull
    List<Byte> getByteList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getByteList);
    }

    @Override
    public @NotNull
    List<Character> getCharacterList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getCharacterList);
    }

    @Override
    public @NotNull
    List<Short> getShortList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getShortList);
    }

    @Override
    public @NotNull
    List<Map<?, ?>> getMapList(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getMapList);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        if (isConfigurationSection(path)) {
            final ConfigurationSection config = getFallbackConfigurationSection(path);
            return clazz.isInstance(config) ? clazz.cast(config) : null;
        }
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getObject(sectionPath, clazz));
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        if (isConfigurationSection(path)) {
            final ConfigurationSection config = getFallbackConfigurationSection(path);
            return clazz.isInstance(config) ? clazz.cast(config) : def;
        }
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getObject(sectionPath, clazz, def));
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz) {
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getSerializable(sectionPath, clazz));
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return getOriginalOrFallback(path, (section, sectionPath) -> section.getSerializable(sectionPath, clazz), def);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getVector);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path, @Nullable final Vector def) {
        return getOriginalOrFallback(path, ConfigurationSection::getVector, def);
    }

    @Override
    public boolean isVector(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isVector);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getOfflinePlayer);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path, @Nullable final OfflinePlayer def) {
        return getOriginalOrFallback(path, ConfigurationSection::getOfflinePlayer, def);
    }

    @Override
    public boolean isOfflinePlayer(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isOfflinePlayer);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getItemStack);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path, @Nullable final ItemStack def) {
        return getOriginalOrFallback(path, ConfigurationSection::getItemStack, def);
    }

    @Override
    public boolean isItemStack(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isItemStack);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getColor);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path, @Nullable final Color def) {
        return getOriginalOrFallback(path, ConfigurationSection::getColor, def);
    }

    @Override
    public boolean isColor(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isColor);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getLocation);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path, @Nullable final Location def) {
        return getOriginalOrFallback(path, ConfigurationSection::getLocation, def);
    }

    @Override
    public boolean isLocation(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isLocation);
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return getFallbackConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::isConfigurationSection);
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
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
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        manager.getOriginal().addDefault(path, value);
    }

    @Override
    public @NotNull
    List<String> getComments(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getComments);
    }

    @Override
    public @NotNull
    List<String> getInlineComments(@NotNull final String path) {
        return getOriginalOrFallback(path, ConfigurationSection::getInlineComments);
    }

    @Override
    public void setComments(@NotNull final String path, @Nullable final List<String> comments) {
        manager.getOriginal().setComments(path, comments);
    }

    @Override
    public void setInlineComments(@NotNull final String path, @Nullable final List<String> comments) {
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
            if (original != null && original.contains(path)) {
                return function.consume(original, path);
            }
            final ConfigurationSection fallback = configs.getRight();
            if (fallback != null && fallback.contains(path)) {
                return function.consume(fallback, path);
            }

            return original == null ? function.consume(fallback, path) : function.consume(original, path);
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
    private <T> T getOriginalOrFallback(final String path, final ConfigurationConsumer<T> function, final T def) {
        return getOriginalOrFallbackWithDefault(path, function, (configs) -> def);
    }

    /**
     * Tries to obtain the set value from the original configuration using {@link ConfigurationSection#isSet(String)}.
     * If it is not present, it tries to obtain it from the
     * fallback configuration. If it is not present there either, it calls the defFunction to obtain the default value.
     *
     * @param path        The path to the value
     * @param function    The function to obtain the default value
     * @param defFunction The function to obtain the default value
     * @param <T>         The type of the value to obtain
     * @return The value
     */
    private <T> T getOriginalOrFallbackWithDefault(final String path, final ConfigurationConsumer<T> function, final Function<Pair<ConfigurationSection, ConfigurationSection>, T> defFunction) {
        final ConfigurationSection original = manager.getOriginal();
        if (original != null && original.isSet(path)) {
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
         * Name of the current {@link ConfigurationSection}
         */
        private final String sectionName;

        /**
         * The original {@link ConfigurationSection}.
         */
        private ConfigurationSection original;

        /**
         * The fallback {@link ConfigurationSection}.
         */
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
        public ConfigManager(@Nullable final String sectionName, @Nullable final ConfigurationSection original, @Nullable final ConfigurationSection fallback) throws IllegalStateException {
            this.sectionName = sectionName;
            this.original = original;
            this.fallback = fallback;
            checkValidState(original, fallback);
        }

        @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
        private void checkValidState(final @Nullable ConfigurationSection original, final @Nullable ConfigurationSection fallback) throws IllegalStateException {
            if (original == null && fallback == null) {
                throw new IllegalStateException("Cannot construct a FallbackConfigurationSection when original and fallback are null");
            }
            if (sectionName != null && !(hasValidName(original) && hasValidName(fallback))) {
                throw new IllegalStateException("Cannot construct a FallbackConfigurationSection when sectionName is not equal to the name of the original or fallback");
            }
        }

        private boolean hasValidName(final ConfigurationSection section) {
            if (section == null) {
                return true;
            }
            final String name = section.getName();
            return name.isEmpty() || name.equals(sectionName);
        }

        /**
         * Checks if the original {@link ConfigurationSection} is up-to-date, updates it if necessary, and returns it.
         *
         * @return The original {@link ConfigurationSection}
         */
        protected ConfigurationSection getOriginal() {
            if (sectionName != null && !checkIsOrphaned()) {
                final ConfigurationSection parentOriginal = parent.manager.getOriginal();
                original = parentOriginal == null ? null : parentOriginal.getConfigurationSection(sectionName);
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
         * Checks if the fallback {@link ConfigurationSection} is up-to-date, updates it if necessary, and returns it.
         *
         * @return The fallback {@link ConfigurationSection}
         */
        protected ConfigurationSection getFallback() {
            if (sectionName != null) {
                final ConfigurationSection parentFallback = parent.manager.getFallback();
                fallback = parentFallback == null ? null : parentFallback.getConfigurationSection(sectionName);
            }
            return fallback;
        }

        @SuppressWarnings("PMD.CompareObjectsWithEquals")
        private boolean checkIsOrphaned() {
            if (original != null && original != original.getRoot()) {
                final ConfigurationSection parent = original.getParent();
                return parent != null && original != parent.getConfigurationSection(sectionName);
            }
            return false;
        }
    }
}
