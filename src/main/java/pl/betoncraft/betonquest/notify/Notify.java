package pl.betoncraft.betonquest.notify;

import org.bukkit.configuration.ConfigurationSection;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;

public class Notify {

    public static NotifyIO get() {
        return get(null, null);
    }

    public static NotifyIO get(final String category) {
        return get(category, null);
    }

    public static NotifyIO get(final Map<String, String> data) {
        return get(null, data);
    }

    public static NotifyIO get(final String category, @Nullable final Map<String, String> data) {
        final SortedSet<String> categories = getCategories(category);

        final Map<String, String> categoryData = getCategorySettings(categories);
        if (data != null) {
            for (final String key : data.keySet()) {
                categoryData.put(key.toLowerCase(Locale.ROOT), data.get(key));
            }
        }

        final List<String> ios = getIOs(categoryData);
        final String configuredIO = BetonQuest.getInstance().getConfig().getString("default_notify_IO");
        if (configuredIO != null) {
            ios.add(configuredIO);
        }
        ios.add("chat");

        try {
            return getNotifyIO(ios, categoryData);
        } catch (final InstructionParseException exception) {
            LogUtils.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        try {
            return new SuppressNotifyIO(categoryData);
        } catch (final InstructionParseException exception) {
            LogUtils.logThrowableReport(exception);
        }
        return null;
    }

    private static SortedSet<String> getCategories(final String category) {
        final SortedSet<String> categories = new TreeSet<>();
        if (category != null) {
            categories.addAll(Arrays.asList(category.split(",")));
        }
        categories.add("default");
        return categories;
    }

    private static Map<String, String> getCategorySettings(SortedSet<String> categories) {
        for (final String packName : Config.getPackages().keySet()) {
            final ConfigurationSection section = Config.getPackages().get(packName).getCustom().getConfig().getConfigurationSection("notifications");
            if (section != null) {
                final SortedSet<String> intersect = new TreeSet<>(categories);
                intersect.retainAll(section.getKeys(false));
                if (intersect.size() > 0) {
                    final ConfigurationSection selectedConfig = section.getConfigurationSection(intersect.first());
                    if (selectedConfig != null && intersect.first().equals(categories.first())) {
                        final Map<String, String> ioData = new HashMap<>();
                        for (final String key : selectedConfig.getKeys(false)) {
                            ioData.put(key.toLowerCase(Locale.ROOT), selectedConfig.getString(key));
                        }
                        return ioData;
                    }
                    categories = categories.subSet(categories.first(), intersect.first());
                }
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
                } catch (final Exception exception) {
                    throw new InstructionParseException("Couldn't load Notify IO '" + name + "': " + exception.getMessage(), exception);
                }
            }
        }
        return null;
    }
}
