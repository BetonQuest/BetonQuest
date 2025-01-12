package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("PMD.CommentRequired")
public final class Notify {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Notify.class);

    private static final Map<String, Map<String, String>> CATEGORY_SETTINGS = new HashMap<>();

    @Nullable
    private static String defaultNotifyIO;

    private Notify() {
    }

    /**
     * Loads the notification settings.
     *
     * @param config the {@link ConfigurationFile} to load from
     */
    public static void load(final ConfigurationFile config) {
        loadCategorySettings();
        defaultNotifyIO = config.getString("default_notify_IO");
    }

    public static NotifyIO get(final QuestPackage pack) {
        return get(pack, null, null);
    }

    public static NotifyIO get(final QuestPackage pack, final String category) {
        return get(pack, category, null);
    }

    public static NotifyIO get(final QuestPackage pack, @Nullable final Map<String, String> data) {
        return get(pack, null, data);
    }

    public static NotifyIO get(final QuestPackage pack, @Nullable final String category, @Nullable final Map<String, String> data) {
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
            return getNotifyIO(pack, ios, categoryData);
        } catch (final QuestException exception) {
            LOG.warn(exception.getMessage(), exception);
        }

        try {
            return new SuppressNotifyIO(pack, categoryData);
        } catch (final QuestException e) {
            LOG.reportException(e);
            throw new UnsupportedOperationException(e);
        }
    }

    private static SortedSet<String> getCategories(@Nullable final String category) {
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
                return new HashMap<>(data);
            }
        }
        return new HashMap<>();
    }

    private static List<String> getIOs(final Map<String, String> categoryData) {
        final List<String> ios = new ArrayList<>();
        if (categoryData.containsKey("io")) {
            ios.addAll(
                    Arrays.stream(categoryData.get("io").split(","))
                            .map(String::trim)
                            .map(o -> o.toLowerCase(Locale.ROOT))
                            .toList());
        }
        return ios;
    }

    private static NotifyIO getNotifyIO(final QuestPackage pack, final List<String> ios, final Map<String, String> categoryData) throws QuestException {
        for (final String name : ios) {
            final Class<? extends NotifyIO> clazz = BetonQuest.getNotifyIO(name);
            if (clazz != null) {
                try {
                    return clazz.getConstructor(QuestPackage.class, Map.class).newInstance(pack, categoryData);
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException
                               | InvocationTargetException exception) {
                    throw new QuestException("Couldn't load Notify IO '" + name + "': " + exception.getMessage(), exception);
                }
            }
        }
        throw new QuestException("No Notify IO could be found, searched for '" + ios + "'!");
    }

    /**
     * The Notifications should be in a separate configuration in the main folder.
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private static void loadCategorySettings() {
        final Map<String, Map<String, String>> settings = new HashMap<>();
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection notifySection = pack.getConfig().getConfigurationSection("notifications");
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
