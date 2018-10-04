package pl.betoncraft.betonquest.notify;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Create a short message
 */
public class Notify {

    public static NotifyIO get(String category) {
        return get(category, new HashMap<>());
    }

    public static NotifyIO get(Map<String, String> data) {
        return get(null, data);
    }

    /**
     * Get a NotifyIO instance
     * @param category comma separated predefined categories
     * @param data Data for IO
     */
    public static NotifyIO get(String category, Map<String,String> data) {

        SortedSet<String> categories = new TreeSet<>();
        if (category != null) {
            categories.addAll(Arrays.asList(category.split(",")));
        }

        // Add default category at end
        categories.add("default");


        // Load from all packages
        ConfigurationSection selectedConfig = null;
        for (String packName : Config.getPackages().keySet()) {
            ConfigPackage pack = Config.getPackages().get(packName);

            if (pack.getCustom().getConfig().contains("notifications")) {
                ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("notifications");

                SortedSet<String> intersect = new TreeSet<>(categories);
                intersect.retainAll(section.getKeys(false));

                // If we match on categories, find the first entry and prune away uninteresting in categories
                if (intersect.size() > 0) {
                    selectedConfig = section.getConfigurationSection(intersect.first());

                    // Found first category, short circuit
                    if (intersect.first().equals(categories.first())) {
                        break;
                    }

                    categories = categories.subSet(categories.first(), intersect.first());
                }
            }
        }

        // Load settings from config if available
        Map<String, String> ioData = new HashMap<>();
        if (selectedConfig != null) {
            for (String key : selectedConfig.getKeys(false)) {
                ioData.put(key.toLowerCase(), selectedConfig.getString(key));
            }
        }

        // Add data over the top
        if (data != null) {
            for (String key : data.keySet()) {
                ioData.put(key.toLowerCase(), data.get(key));
            }
        }

        // NotifyIO's to use
        List<String> ios = new ArrayList<>();

        // If data contains the key 'io' then we parse it as a comma separated list of io's to use.
        if (ioData.containsKey("io")) {
            ios.addAll(Arrays.asList(
                    Arrays.stream(ioData.get("io").split(","))
                            .map(String::trim)
                            .toArray(String[]::new)));
        }

        // Add default IO, if one
        String configuredIO = BetonQuest.getInstance().getConfig().getString("default_notify_IO");
        if (configuredIO != null) {
            ios.add(configuredIO);
        }

        // Add fallbacks
        ios.add("chat");

        // Load IO
        NotifyIO tio = null;
        for (String name : ios) {
            Class<? extends NotifyIO> c = BetonQuest.getNotifyIO(name);
            if (c != null) {
                try {
                    tio = c.getConstructor(Map.class).newInstance(ioData);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    Debug.error("Error when loading notify IO");
                    return new DummyIO(ioData);
                }
                break;
            }
        }

        if (tio == null) {
            Debug.error("Error when loading notify IO");
            return new DummyIO(ioData);
        }

        return tio;
    }

    public static NotifyIO get() {
        return get(new HashMap<>());
    }

    // Fallback dummy IO
    public static class DummyIO extends NotifyIO {

        public DummyIO(Map<String, String> data) {
            super(data);
        }

        @Override
        public void sendNotify(String message, Collection<? extends Player> players) {
        }
    }

}
