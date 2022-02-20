package org.betonquest.betonquest.api.bukkit.config.custom;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class hides an original {@link ConfigurationSection} and exposes it,
 * as if it were the original {@link ConfigurationSection}.
 * This makes it possible to change parts of the original {@link ConfigurationSection}'s behaviour,
 * without the need to implement everything all over again.
 * <p>
 * This class is necessary because multiple implementations of the Bukkit Configuration API may exist.
 * It enables the extending classes to work with all implementations.
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
public class ConfigurationSectionDecorator implements ConfigurationSection {
    /**
     * The original {@link ConfigurationSection}.
     */
    protected final ConfigurationSection original;

    /**
     * Create a new decorator instance.
     *
     * @param original The original {@link ConfigurationSection} that should be decorated.
     */
    public ConfigurationSectionDecorator(final ConfigurationSection original) {
        this.original = original;
    }

    @Override
    public @NotNull
    Set<String> getKeys(final boolean deep) {
        return original.getKeys(deep);
    }

    @Override
    public @NotNull
    Map<String, Object> getValues(final boolean deep) {
        return original.getValues(deep);
    }

    @Override
    public boolean contains(@NotNull final String path) {
        return original.contains(path);
    }

    @Override
    public boolean contains(@NotNull final String path, final boolean ignoreDefault) {
        return original.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(@NotNull final String path) {
        return original.isSet(path);
    }

    @Override
    public @Nullable
    String getCurrentPath() {
        return original.getCurrentPath();
    }

    @Override
    public @NotNull
    String getName() {
        return original.getName();
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return original.getRoot();
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return original.getParent();
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return original.get(path);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return original.get(path, def);
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        original.set(path, value);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        return original.createSection(path);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        return original.createSection(path, map);
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path) {
        return original.getString(path);
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path, @Nullable final String def) {
        return original.getString(path, def);
    }

    @Override
    public boolean isString(@NotNull final String path) {
        return original.isString(path);
    }

    @Override
    public int getInt(@NotNull final String path) {
        return original.getInt(path);
    }

    @Override
    public int getInt(@NotNull final String path, final int def) {
        return original.getInt(path, def);
    }

    @Override
    public boolean isInt(@NotNull final String path) {
        return original.isInt(path);
    }

    @Override
    public boolean getBoolean(@NotNull final String path) {
        return original.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull final String path, final boolean def) {
        return original.getBoolean(path, def);
    }

    @Override
    public boolean isBoolean(@NotNull final String path) {
        return original.isBoolean(path);
    }

    @Override
    public double getDouble(@NotNull final String path) {
        return original.getDouble(path);
    }

    @Override
    public double getDouble(@NotNull final String path, final double def) {
        return original.getDouble(path, def);
    }

    @Override
    public boolean isDouble(@NotNull final String path) {
        return original.isDouble(path);
    }

    @Override
    public long getLong(@NotNull final String path) {
        return original.getLong(path);
    }

    @Override
    public long getLong(@NotNull final String path, final long def) {
        return original.getLong(path, def);
    }

    @Override
    public boolean isLong(@NotNull final String path) {
        return original.isLong(path);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path) {
        return original.getList(path);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path, @Nullable final List<?> def) {
        return original.getList(path, def);
    }

    @Override
    public boolean isList(@NotNull final String path) {
        return original.isList(path);
    }

    @Override
    public @NotNull
    List<String> getStringList(@NotNull final String path) {
        return original.getStringList(path);
    }

    @Override
    public @NotNull
    List<Integer> getIntegerList(@NotNull final String path) {
        return original.getIntegerList(path);
    }

    @Override
    public @NotNull
    List<Boolean> getBooleanList(@NotNull final String path) {
        return original.getBooleanList(path);
    }

    @Override
    public @NotNull
    List<Double> getDoubleList(@NotNull final String path) {
        return original.getDoubleList(path);
    }

    @Override
    public @NotNull
    List<Float> getFloatList(@NotNull final String path) {
        return original.getFloatList(path);
    }

    @Override
    public @NotNull
    List<Long> getLongList(@NotNull final String path) {
        return original.getLongList(path);
    }

    @Override
    public @NotNull
    List<Byte> getByteList(@NotNull final String path) {
        return original.getByteList(path);
    }

    @Override
    public @NotNull
    List<Character> getCharacterList(@NotNull final String path) {
        return original.getCharacterList(path);
    }

    @Override
    public @NotNull
    List<Short> getShortList(@NotNull final String path) {
        return original.getShortList(path);
    }

    @Override
    public @NotNull
    List<Map<?, ?>> getMapList(@NotNull final String path) {
        return original.getMapList(path);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        return original.getObject(path, clazz);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return original.getObject(path, clazz, def);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz) {
        return original.getSerializable(path, clazz);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return original.getSerializable(path, clazz, def);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path) {
        return original.getVector(path);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path, @Nullable final Vector def) {
        return original.getVector(path, def);
    }

    @Override
    public boolean isVector(@NotNull final String path) {
        return original.isVector(path);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path) {
        return original.getOfflinePlayer(path);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path, @Nullable final OfflinePlayer def) {
        return original.getOfflinePlayer(path, def);
    }

    @Override
    public boolean isOfflinePlayer(@NotNull final String path) {
        return original.isOfflinePlayer(path);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path) {
        return original.getItemStack(path);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path, @Nullable final ItemStack def) {
        return original.getItemStack(path, def);
    }

    @Override
    public boolean isItemStack(@NotNull final String path) {
        return original.isItemStack(path);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path) {
        return original.getColor(path);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path, @Nullable final Color def) {
        return original.getColor(path, def);
    }

    @Override
    public boolean isColor(@NotNull final String path) {
        return original.isColor(path);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path) {
        return original.getLocation(path);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path, @Nullable final Location def) {
        return original.getLocation(path, def);
    }

    @Override
    public boolean isLocation(@NotNull final String path) {
        return original.isLocation(path);
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return original.getConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(@NotNull final String path) {
        return original.isConfigurationSection(path);
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return original.getDefaultSection();
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        original.addDefault(path, value);
    }

    @Override
    public @NotNull
    List<String> getComments(@NotNull final String path) {
        return original.getComments(path);
    }

    @Override
    public @NotNull
    List<String> getInlineComments(@NotNull final String path) {
        return original.getInlineComments(path);
    }

    @Override
    public void setComments(@NotNull final String path, @Nullable final List<String> comments) {
        original.setComments(path, comments);
    }

    @Override
    public void setInlineComments(@NotNull final String path, @Nullable final List<String> comments) {
        original.setInlineComments(path, comments);
    }
}
