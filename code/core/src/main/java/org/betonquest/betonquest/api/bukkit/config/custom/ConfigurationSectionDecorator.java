package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class hides an original {@link ConfigurationSection} and exposes it,
 * as if it were the original {@link ConfigurationSection}.
 * This makes it possible to change parts of the original {@link ConfigurationSection}'s behavior,
 * without the need to implement everything all over again.
 * <p>
 * This class is necessary because multiple implementations of the Bukkit Configuration API may exist.
 * It enables the extending classes to work with all implementations.
 */
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.CouplingBetweenObjects"})
public class ConfigurationSectionDecorator implements ConfigurationSection {
    /**
     * The original {@link ConfigurationSection}.
     */
    protected ConfigurationSection original;

    /**
     * Create a new decorator instance.
     *
     * @param original The original {@link ConfigurationSection} that should be decorated.
     */
    public ConfigurationSectionDecorator(final ConfigurationSection original) {
        this.original = original;
    }

    @Override
    public Set<String> getKeys(final boolean deep) {
        return original.getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(final boolean deep) {
        return original.getValues(deep);
    }

    @Override
    public boolean contains(final String path) {
        return original.contains(path);
    }

    @Override
    public boolean contains(final String path, final boolean ignoreDefault) {
        return original.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(final String path) {
        return original.isSet(path);
    }

    @Override
    @Nullable
    public String getCurrentPath() {
        return original.getCurrentPath();
    }

    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    @Nullable
    public Configuration getRoot() {
        return original.getRoot();
    }

    @Override
    @Nullable
    public ConfigurationSection getParent() {
        return original.getParent();
    }

    @Override
    @Nullable
    public Object get(final String path) {
        return original.get(path);
    }

    @Override
    @Nullable
    public Object get(final String path, @Nullable final Object def) {
        return original.get(path, def);
    }

    @Override
    public void set(final String path, @Nullable final Object value) {
        original.set(path, value);
    }

    @Override
    public ConfigurationSection createSection(final String path) {
        return original.createSection(path);
    }

    @Override
    public ConfigurationSection createSection(final String path, final Map<?, ?> map) {
        return original.createSection(path, map);
    }

    @Override
    @Nullable
    public String getString(final String path) {
        return original.getString(path);
    }

    @Override
    @Nullable
    public String getString(final String path, @Nullable final String def) {
        return original.getString(path, def);
    }

    @Override
    public boolean isString(final String path) {
        return original.isString(path);
    }

    @Override
    public int getInt(final String path) {
        return original.getInt(path);
    }

    @Override
    public int getInt(final String path, final int def) {
        return original.getInt(path, def);
    }

    @Override
    public boolean isInt(final String path) {
        return original.isInt(path);
    }

    @Override
    public boolean getBoolean(final String path) {
        return original.getBoolean(path);
    }

    @Override
    public boolean getBoolean(final String path, final boolean def) {
        return original.getBoolean(path, def);
    }

    @Override
    public boolean isBoolean(final String path) {
        return original.isBoolean(path);
    }

    @Override
    public double getDouble(final String path) {
        return original.getDouble(path);
    }

    @Override
    public double getDouble(final String path, final double def) {
        return original.getDouble(path, def);
    }

    @Override
    public boolean isDouble(final String path) {
        return original.isDouble(path);
    }

    @Override
    public long getLong(final String path) {
        return original.getLong(path);
    }

    @Override
    public long getLong(final String path, final long def) {
        return original.getLong(path, def);
    }

    @Override
    public boolean isLong(final String path) {
        return original.isLong(path);
    }

    @Override
    @Nullable
    public List<?> getList(final String path) {
        return original.getList(path);
    }

    @Override
    @Nullable
    public List<?> getList(final String path, @Nullable final List<?> def) {
        return original.getList(path, def);
    }

    @Override
    public boolean isList(final String path) {
        return original.isList(path);
    }

    @Override
    public List<String> getStringList(final String path) {
        return original.getStringList(path);
    }

    @Override
    public List<Integer> getIntegerList(final String path) {
        return original.getIntegerList(path);
    }

    @Override
    public List<Boolean> getBooleanList(final String path) {
        return original.getBooleanList(path);
    }

    @Override
    public List<Double> getDoubleList(final String path) {
        return original.getDoubleList(path);
    }

    @Override
    public List<Float> getFloatList(final String path) {
        return original.getFloatList(path);
    }

    @Override
    public List<Long> getLongList(final String path) {
        return original.getLongList(path);
    }

    @Override
    public List<Byte> getByteList(final String path) {
        return original.getByteList(path);
    }

    @Override
    public List<Character> getCharacterList(final String path) {
        return original.getCharacterList(path);
    }

    @Override
    public List<Short> getShortList(final String path) {
        return original.getShortList(path);
    }

    @Override
    public List<Map<?, ?>> getMapList(final String path) {
        return original.getMapList(path);
    }

    @Override
    public <T> T getObject(final String path, final Class<T> clazz) {
        return original.getObject(path, clazz);
    }

    @Override
    public <T> T getObject(final String path, final Class<T> clazz, @Nullable final T def) {
        return original.getObject(path, clazz, def);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
        return original.getSerializable(path, clazz);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz, @Nullable final T def) {
        return original.getSerializable(path, clazz, def);
    }

    @Override
    @Nullable
    public Vector getVector(final String path) {
        return original.getVector(path);
    }

    @Override
    @Nullable
    public Vector getVector(final String path, @Nullable final Vector def) {
        return original.getVector(path, def);
    }

    @Override
    public boolean isVector(final String path) {
        return original.isVector(path);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(final String path) {
        return original.getOfflinePlayer(path);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(final String path, @Nullable final OfflinePlayer def) {
        return original.getOfflinePlayer(path, def);
    }

    @Override
    public boolean isOfflinePlayer(final String path) {
        return original.isOfflinePlayer(path);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(final String path) {
        return original.getItemStack(path);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(final String path, @Nullable final ItemStack def) {
        return original.getItemStack(path, def);
    }

    @Override
    public boolean isItemStack(final String path) {
        return original.isItemStack(path);
    }

    @Override
    @Nullable
    public Color getColor(final String path) {
        return original.getColor(path);
    }

    @Override
    @Nullable
    public Color getColor(final String path, @Nullable final Color def) {
        return original.getColor(path, def);
    }

    @Override
    public boolean isColor(final String path) {
        return original.isColor(path);
    }

    @Override
    @Nullable
    public Location getLocation(final String path) {
        return original.getLocation(path);
    }

    @Override
    @Nullable
    public Location getLocation(final String path, @Nullable final Location def) {
        return original.getLocation(path, def);
    }

    @Override
    public boolean isLocation(final String path) {
        return original.isLocation(path);
    }

    @Override
    @Nullable
    public ConfigurationSection getConfigurationSection(final String path) {
        return original.getConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(final String path) {
        return original.isConfigurationSection(path);
    }

    @Override
    @Nullable
    public ConfigurationSection getDefaultSection() {
        return original.getDefaultSection();
    }

    @Override
    public void addDefault(final String path, @Nullable final Object value) {
        original.addDefault(path, value);
    }

    @Override
    public List<String> getComments(final String path) {
        return original.getComments(path);
    }

    @Override
    public List<String> getInlineComments(final String path) {
        return original.getInlineComments(path);
    }

    @Override
    public void setComments(final String path, @Nullable final List<String> comments) {
        original.setComments(path, comments);
    }

    @Override
    public void setInlineComments(final String path, @Nullable final List<String> comments) {
        original.setInlineComments(path, comments);
    }

    @Override
    public String toString() {
        final Configuration root = this.getRoot();
        return this.getClass().getSimpleName() + "[path='" + this.getCurrentPath() + "', root='" + (root == null ? null : root.getClass().getSimpleName()) + "']";
    }
}
