package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Handles and stores notification settings.
 */
public final class Notify {

    /**
     * Loaded custom settings for different notification categories.
     */
    private static final Map<String, Map<String, String>> CATEGORY_SETTINGS = new HashMap<>();

    /**
     * Default custom notification io to use as fallback.
     * If not set the "chat" io is used.
     */
    @Nullable
    private static String defaultNotifyIO;

    private Notify() {
    }

    /**
     * Loads the notification settings.
     *
     * @param config   the {@link ConfigAccessor} to load from
     * @param packages the quest packages to load from
     */
    public static void load(final ConfigAccessor config, final Collection<QuestPackage> packages) {
        loadCategorySettings(packages);
        defaultNotifyIO = config.getString("default_notify_IO");
    }

    /**
     * Gets the configured notify IO.
     *
     * @param pack     the pack to get from
     * @param category the custom category
     * @return the parsed NNotify IO
     * @throws QuestException when the notify IO could not be created
     */
    public static NotifyIO get(@Nullable final QuestPackage pack, @Nullable final String category) throws QuestException {
        return get(pack, category, null);
    }

    /**
     * Gets the configured notify IO.
     *
     * @param pack     the pack to get from
     * @param category the custom category
     * @param data     the custom data to use for notification
     * @return the parsed Notify IO
     * @throws QuestException when the Notify IO could not be created with the data
     */
    public static NotifyIO get(@Nullable final QuestPackage pack, @Nullable final String category,
                               @Nullable final Map<String, String> data) throws QuestException {
        final Map<String, String> categoryData = getCategorySettings(getCategories(category));
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

        return BetonQuest.getInstance().getFeatureRegistries().notifyIO().getFactory(ios).create(pack, categoryData);
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
            for (final String part : categoryData.get("io").split(",")) {
                ios.add(part.trim().toLowerCase(Locale.ROOT));
            }
        }
        return ios;
    }

    /**
     * The Notifications should be in a separate configuration in the main folder.
     */
    private static void loadCategorySettings(final Collection<QuestPackage> packages) {
        CATEGORY_SETTINGS.clear();
        for (final QuestPackage pack : packages) {
            final ConfigurationSection notifySection = pack.getConfig().getConfigurationSection("notifications");
            if (notifySection == null) {
                continue;
            }
            for (final String notifyName : notifySection.getKeys(false)) {
                final ConfigurationSection notify = notifySection.getConfigurationSection(notifyName);
                if (notify != null && !CATEGORY_SETTINGS.containsKey(notifyName)) {
                    final Map<String, String> data = new HashMap<>();
                    for (final String key : notify.getKeys(false)) {
                        data.put(key.toLowerCase(Locale.ROOT), notify.getString(key));
                    }
                    CATEGORY_SETTINGS.put(notifyName, data);
                }
            }
        }
    }
}
