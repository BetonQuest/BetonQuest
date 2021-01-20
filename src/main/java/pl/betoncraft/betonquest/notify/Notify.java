package pl.betoncraft.betonquest.notify;

import org.bukkit.configuration.ConfigurationSection;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
public final class Notify {
    private static final Map<String, Map<String, String>> CATEGORY_SETTINGS = new HashMap<>();
    private static String defaultNotifyIO;

    private Notify() {
    }

    public static void load() {
        loadCategorySettings();
        defaultNotifyIO = BetonQuest.getInstance().getConfig().getString("default_notify_IO");
    }

    public static NotifyIO get() {
        return get(null, null);
    }

    public static NotifyIO get(final String category) {
        return get(category, null);
    }

    public static NotifyIO get(@Nullable final Map<String, String> data) {
        return get(null, data);
    }

    public static NotifyIO get(final String category, @Nullable final Map<String, String> data) {
        final SortedSet<String> categories = getCategories(category);

        final Map<String, String> categoryData = getCategorySettings(categories);
        if (data != null) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {
                categoryData.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
            }
        }

        final List<String> ios = getIOs(categoryData);
        if (defaultNotifyIO != null) {
            ios.add(defaultNotifyIO);
        }
        ios.add("chat");

        try {
            return getNotifyIO(ios, categoryData);
        } catch (final InstructionParseException exception) {
            LogUtils.getLogger().log(Level.WARNING, exception.getMessage(), exception);
        }

        try {
            return new SuppressNotifyIO(categoryData);
        } catch (final InstructionParseException exception) {
            LogUtils.logThrowableReport(exception);
            throw new UnsupportedOperationException(exception);
        }
    }

    private static SortedSet<String> getCategories(final String category) {
        final SortedSet<String> categories = new TreeSet<>();
        if (category != null) {
            categories.addAll(Arrays.asList(category.split(",")));
        }
        categories.add("default");
        return categories;
    }

    private static Map<String, String> getCategorySettings(final SortedSet<String> categories) {
        for (final String category : categories) {
            final Map<String, String> data = CATEGORY_SETTINGS.get(category);
            if (data != null) {
                return data;
            }
        }
        return new HashMap<>();
    }

    private static List<String> getIOs(final Map<String, String> categoryData) {
        final List<String> ios = new ArrayList<>();
        if (categoryData.containsKey("io")) {
            ios.addAll(Arrays.asList(
                    Arrays.stream(categoryData.get("io").split(","))
                            .map(String::trim)
                            .toArray(String[]::new)));
        }
        return ios;
    }

    private static NotifyIO getNotifyIO(final List<String> ios, final Map<String, String> categoryData) throws InstructionParseException {
        for (final String name : ios) {
            final Class<? extends NotifyIO> clazz = BetonQuest.getNotifyIO(name);
            if (clazz != null) {
                try {
                    return clazz.getConstructor(Map.class).newInstance(categoryData);
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                    throw new InstructionParseException("Couldn't load Notify IO '" + name + "': " + exception.getMessage(), exception);
                }
            }
        }
        throw new InstructionParseException("No Notify IO could be found, searched for '" + ios + "'!");
    }

    /**
     * The Notifications should be in a separate configuration in the main folder
     */
    // TODO Replace with new new method
    private static void loadCategorySettings() {
        final Map<String, Map<String, String>> settings = new HashMap<>();
        for (final String packName : Config.getPackages().keySet()) {
            final ConfigurationSection notifySection = Config.getPackages().get(packName).getCustom().getConfig().getConfigurationSection("notifications");
            if (notifySection != null) {
                for (final String notifyName : notifySection.getKeys(false)) {
                    final ConfigurationSection notify = notifySection.getConfigurationSection(notifyName);
                    if (notify != null && !settings.containsKey(notifyName)) {
                        final Map<String, String> data = new HashMap<>();
                        for (final String key : notify.getKeys(false)) {
                            data.put(key.toLowerCase(Locale.ROOT), notify.getString(key));
                        }
                        settings.put(notifyName, data);
                    }
                }
            }
        }
        CATEGORY_SETTINGS.clear();
        CATEGORY_SETTINGS.putAll(settings);
    }
}
