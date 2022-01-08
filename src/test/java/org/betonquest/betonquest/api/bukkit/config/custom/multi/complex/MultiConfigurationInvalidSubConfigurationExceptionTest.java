package org.betonquest.betonquest.api.bukkit.config.custom.multi.complex;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiConfiguration} and it's thrown {@link InvalidSubConfigurationException}s.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class MultiConfigurationInvalidSubConfigurationExceptionTest {

    /**
     * Empty constructor
     */
    public MultiConfigurationInvalidSubConfigurationExceptionTest() {
        super();
    }

    @Test
    void testInvalidPathSeparator() {
        final YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().pathSeparator('/');
        try {
            new MultiConfiguration(configuration);
        } catch (final InvalidSubConfigurationException e) {
            assertEquals("At least one source config does not have valid path separator!", e.getMessage());
            return;
        } catch (final KeyConflictException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @Test
    void testNoRoot() {
        try {
            new MultiConfiguration(new FakeConfigurationSection());
        } catch (final InvalidSubConfigurationException e) {
            assertEquals("At least one source config does not have a root!", e.getMessage());
            return;
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @SuppressWarnings({"PMD.UncommentedEmptyMethodBody", "PMD.ReturnEmptyCollectionRatherThanNull", "PMD.CommentRequired"})
    private static class FakeConfigurationSection implements ConfigurationSection {
        @Override
        public @NotNull
        Set<String> getKeys(final boolean deep) {
            return null;
        }

        @Override
        public @NotNull
        Map<String, Object> getValues(final boolean deep) {
            return null;
        }

        @Override
        public boolean contains(@NotNull final String path) {
            return false;
        }

        @Override
        public boolean contains(@NotNull final String path, final boolean ignoreDefault) {
            return false;
        }

        @Override
        public boolean isSet(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        String getCurrentPath() {
            return null;
        }

        @Override
        public @NotNull
        String getName() {
            return null;
        }

        @Override
        public @Nullable
        Configuration getRoot() {
            return null;
        }

        @Override
        public @Nullable
        ConfigurationSection getParent() {
            return null;
        }

        @Override
        public @Nullable
        Object get(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        Object get(@NotNull final String path, @Nullable final Object def) {
            return null;
        }

        @Override
        public void set(@NotNull final String path, @Nullable final Object value) {

        }

        @Override
        public @NotNull
        ConfigurationSection createSection(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
            return null;
        }

        @Override
        public @Nullable
        String getString(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        String getString(@NotNull final String path, @Nullable final String def) {
            return null;
        }

        @Override
        public boolean isString(@NotNull final String path) {
            return false;
        }

        @Override
        public int getInt(@NotNull final String path) {
            return 0;
        }

        @Override
        public int getInt(@NotNull final String path, final int def) {
            return 0;
        }

        @Override
        public boolean isInt(@NotNull final String path) {
            return false;
        }

        @Override
        public boolean getBoolean(@NotNull final String path) {
            return false;
        }

        @Override
        public boolean getBoolean(@NotNull final String path, final boolean def) {
            return false;
        }

        @Override
        public boolean isBoolean(@NotNull final String path) {
            return false;
        }

        @Override
        public double getDouble(@NotNull final String path) {
            return 0;
        }

        @Override
        public double getDouble(@NotNull final String path, final double def) {
            return 0;
        }

        @Override
        public boolean isDouble(@NotNull final String path) {
            return false;
        }

        @Override
        public long getLong(@NotNull final String path) {
            return 0;
        }

        @Override
        public long getLong(@NotNull final String path, final long def) {
            return 0;
        }

        @Override
        public boolean isLong(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        List<?> getList(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        List<?> getList(@NotNull final String path, @Nullable final List<?> def) {
            return null;
        }

        @Override
        public boolean isList(@NotNull final String path) {
            return false;
        }

        @Override
        public @NotNull
        List<String> getStringList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Integer> getIntegerList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Boolean> getBooleanList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Double> getDoubleList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Float> getFloatList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Long> getLongList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Byte> getByteList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Character> getCharacterList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Short> getShortList(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<Map<?, ?>> getMapList(@NotNull final String path) {
            return null;
        }

        @Override
        public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
            return null;
        }

        @Override
        public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
            return null;
        }

        @Override
        public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ConfigurationSerializable> T getSerializable(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
            return null;
        }

        @Override
        public @Nullable
        Vector getVector(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        Vector getVector(@NotNull final String path, @Nullable final Vector def) {
            return null;
        }

        @Override
        public boolean isVector(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        OfflinePlayer getOfflinePlayer(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        OfflinePlayer getOfflinePlayer(@NotNull final String path, @Nullable final OfflinePlayer def) {
            return null;
        }

        @Override
        public boolean isOfflinePlayer(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        ItemStack getItemStack(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        ItemStack getItemStack(@NotNull final String path, @Nullable final ItemStack def) {
            return null;
        }

        @Override
        public boolean isItemStack(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        Color getColor(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        Color getColor(@NotNull final String path, @Nullable final Color def) {
            return null;
        }

        @Override
        public boolean isColor(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        Location getLocation(@NotNull final String path) {
            return null;
        }

        @Override
        public @Nullable
        Location getLocation(@NotNull final String path, @Nullable final Location def) {
            return null;
        }

        @Override
        public boolean isLocation(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        ConfigurationSection getConfigurationSection(@NotNull final String path) {
            return null;
        }

        @Override
        public boolean isConfigurationSection(@NotNull final String path) {
            return false;
        }

        @Override
        public @Nullable
        ConfigurationSection getDefaultSection() {
            return null;
        }

        @Override
        public void addDefault(@NotNull final String path, @Nullable final Object value) {

        }

        @Override
        public @NotNull
        List<String> getComments(@NotNull final String path) {
            return null;
        }

        @Override
        public @NotNull
        List<String> getInlineComments(@NotNull final String path) {
            return null;
        }

        @Override
        public void setComments(@NotNull final String path, @Nullable final List<String> comments) {

        }

        @Override
        public void setInlineComments(@NotNull final String path, @Nullable final List<String> comments) {

        }
    }
}
