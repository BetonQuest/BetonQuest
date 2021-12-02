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
 * This class hides an original {@link ConfigurationSection} and expose it,
 * like it is the original {@link ConfigurationSection}.
 * This gives the possibility to change a partial behaviour of the original {@link ConfigurationSection},
 * without the need to implement everything again and again.
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
public class ConfigurationSectionDecorator implements ConfigurationSection {
    /**
     * The original {@link ConfigurationSection}.
     */
    protected final ConfigurationSection delegate;

    /**
     * Create a new decorator instance.
     * @param delegate The original {@link ConfigurationSection} that should be decorated.
     */
    public ConfigurationSectionDecorator(final ConfigurationSection delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull
    Set<String> getKeys(final boolean deep) {
        return delegate.getKeys(deep);
    }

    @Override
    public @NotNull
    Map<String, Object> getValues(final boolean deep) {
        return delegate.getValues(deep);
    }

    @Override
    public boolean contains(@NotNull final String path) {
        return delegate.contains(path);
    }

    @Override
    public boolean contains(@NotNull final String path, final boolean ignoreDefault) {
        return delegate.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(@NotNull final String path) {
        return delegate.isSet(path);
    }

    @Override
    public @Nullable
    String getCurrentPath() {
        return delegate.getCurrentPath();
    }

    @Override
    public @NotNull
    String getName() {
        return delegate.getName();
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return delegate.getRoot();
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return delegate.getParent();
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return delegate.get(path);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return delegate.get(path, def);
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        delegate.set(path, value);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        return delegate.createSection(path);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        return delegate.createSection(path, map);
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path) {
        return delegate.getString(path);
    }

    @Override
    public @Nullable
    String getString(@NotNull final String path, @Nullable final String def) {
        return delegate.getString(path, def);
    }

    @Override
    public boolean isString(@NotNull final String path) {
        return delegate.isString(path);
    }

    @Override
    public int getInt(@NotNull final String path) {
        return delegate.getInt(path);
    }

    @Override
    public int getInt(@NotNull final String path, final int def) {
        return delegate.getInt(path, def);
    }

    @Override
    public boolean isInt(@NotNull final String path) {
        return delegate.isInt(path);
    }

    @Override
    public boolean getBoolean(@NotNull final String path) {
        return delegate.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull final String path, final boolean def) {
        return delegate.getBoolean(path, def);
    }

    @Override
    public boolean isBoolean(@NotNull final String path) {
        return delegate.isBoolean(path);
    }

    @Override
    public double getDouble(@NotNull final String path) {
        return delegate.getDouble(path);
    }

    @Override
    public double getDouble(@NotNull final String path, final double def) {
        return delegate.getDouble(path, def);
    }

    @Override
    public boolean isDouble(@NotNull final String path) {
        return delegate.isDouble(path);
    }

    @Override
    public long getLong(@NotNull final String path) {
        return delegate.getLong(path);
    }

    @Override
    public long getLong(@NotNull final String path, final long def) {
        return delegate.getLong(path, def);
    }

    @Override
    public boolean isLong(@NotNull final String path) {
        return delegate.isLong(path);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path) {
        return delegate.getList(path);
    }

    @Override
    public @Nullable
    List<?> getList(@NotNull final String path, @Nullable final List<?> def) {
        return delegate.getList(path, def);
    }

    @Override
    public boolean isList(@NotNull final String path) {
        return delegate.isList(path);
    }

    @Override
    public @NotNull
    List<String> getStringList(@NotNull final String path) {
        return delegate.getStringList(path);
    }

    @Override
    public @NotNull
    List<Integer> getIntegerList(@NotNull final String path) {
        return delegate.getIntegerList(path);
    }

    @Override
    public @NotNull
    List<Boolean> getBooleanList(@NotNull final String path) {
        return delegate.getBooleanList(path);
    }

    @Override
    public @NotNull
    List<Double> getDoubleList(@NotNull final String path) {
        return delegate.getDoubleList(path);
    }

    @Override
    public @NotNull
    List<Float> getFloatList(@NotNull final String path) {
        return delegate.getFloatList(path);
    }

    @Override
    public @NotNull
    List<Long> getLongList(@NotNull final String path) {
        return delegate.getLongList(path);
    }

    @Override
    public @NotNull
    List<Byte> getByteList(@NotNull final String path) {
        return delegate.getByteList(path);
    }

    @Override
    public @NotNull
    List<Character> getCharacterList(@NotNull final String path) {
        return delegate.getCharacterList(path);
    }

    @Override
    public @NotNull
    List<Short> getShortList(@NotNull final String path) {
        return delegate.getShortList(path);
    }

    @Override
    public @NotNull
    List<Map<?, ?>> getMapList(@NotNull final String path) {
        return delegate.getMapList(path);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        return delegate.getObject(path, clazz);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return delegate.getObject(path, clazz, def);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz) {
        return delegate.getSerializable(path, clazz);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return delegate.getSerializable(path, clazz, def);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path) {
        return delegate.getVector(path);
    }

    @Override
    public @Nullable
    Vector getVector(@NotNull final String path, @Nullable final Vector def) {
        return delegate.getVector(path, def);
    }

    @Override
    public boolean isVector(@NotNull final String path) {
        return delegate.isVector(path);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path) {
        return delegate.getOfflinePlayer(path);
    }

    @Override
    public @Nullable
    OfflinePlayer getOfflinePlayer(@NotNull final String path, @Nullable final OfflinePlayer def) {
        return delegate.getOfflinePlayer(path, def);
    }

    @Override
    public boolean isOfflinePlayer(@NotNull final String path) {
        return delegate.isOfflinePlayer(path);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path) {
        return delegate.getItemStack(path);
    }

    @Override
    public @Nullable
    ItemStack getItemStack(@NotNull final String path, @Nullable final ItemStack def) {
        return delegate.getItemStack(path, def);
    }

    @Override
    public boolean isItemStack(@NotNull final String path) {
        return delegate.isItemStack(path);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path) {
        return delegate.getColor(path);
    }

    @Override
    public @Nullable
    Color getColor(@NotNull final String path, @Nullable final Color def) {
        return delegate.getColor(path, def);
    }

    @Override
    public boolean isColor(@NotNull final String path) {
        return delegate.isColor(path);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path) {
        return delegate.getLocation(path);
    }

    @Override
    public @Nullable
    Location getLocation(@NotNull final String path, @Nullable final Location def) {
        return delegate.getLocation(path, def);
    }

    @Override
    public boolean isLocation(@NotNull final String path) {
        return delegate.isLocation(path);
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return delegate.getConfigurationSection(path);
    }

    @Override
    public boolean isConfigurationSection(@NotNull final String path) {
        return delegate.isConfigurationSection(path);
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return delegate.getDefaultSection();
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        delegate.addDefault(path, value);
    }
}
