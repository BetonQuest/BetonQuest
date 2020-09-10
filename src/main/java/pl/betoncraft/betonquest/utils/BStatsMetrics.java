package pl.betoncraft.betonquest.utils;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.id.*;

import java.util.*;

public class BStatsMetrics {
    private final static int METRICS_ID = 551;

    private final Metrics metrics;
    private final JavaPlugin plugin;

    public BStatsMetrics(final JavaPlugin plugin,
                         final HashMap<ConditionID, Condition> conditions,
                         final HashMap<EventID, QuestEvent> events,
                         final HashMap<ObjectiveID, Objective> objectives,
                         final HashMap<VariableID, Variable> variables,
                         final HashMap<String, Class<? extends Condition>> conditionTypes,
                         final HashMap<String, Class<? extends QuestEvent>> eventTypes,
                         final HashMap<String, Class<? extends Objective>> objectiveTypes,
                         final HashMap<String, Class<? extends Variable>> variableTypes) {
        this.plugin = plugin;
        metrics = new Metrics(plugin, METRICS_ID);

        versionMcBq();
        listUsage("conditions", conditions, conditionTypes);
        listUsage("events", events, eventTypes);
        listUsage("objectives", objectives, objectiveTypes);
        listUsage("variables", variables, variableTypes);
        hookedPlugins();
    }

    private void versionMcBq() {
        final String versionPlugin = plugin.getDescription().getVersion();
        final String versionMc = plugin.getServer().getBukkitVersion().split("-")[0];

        metrics.addCustomChart(new Metrics.DrilldownPie("versionMcBq", () -> getDrilldownPie(versionPlugin, versionMc)));
        metrics.addCustomChart(new Metrics.DrilldownPie("versionBqMc", () -> getDrilldownPie(versionMc, versionPlugin)));
    }

    public Map<String, Map<String, Integer>> getDrilldownPie(final String value1, final String value2) {
        final Map<String, Map<String, Integer>> map = new HashMap<>();
        final Map<String, Integer> entry = new HashMap<>();
        entry.put(value1, 1);
        map.put(value2, entry);
        return map;
    }

    private <T> void listUsage(final String bStatsId, final HashMap<? extends ID, ? extends T> objects, final HashMap<String, Class<? extends T>> types) {
        metrics.addCustomChart(new Metrics.DrilldownPie(bStatsId, () -> {
            final HashMap<String, Integer> usageList = countUsageClasses(objects.values(), types);
            final Map<String, Map<String, Integer>> map = new HashMap<>();
            for (final Map.Entry<String, Integer> usage : usageList.entrySet()) {
                final HashMap<String, Integer> usageValue = new HashMap<>();
                usageValue.put(usage.getKey(), usage.getValue());
                map.put(usage.getKey(), usageValue);
            }
            return map;
        }));
    }

    private <T> HashMap<String, Integer> countUsageClasses(final Collection<? extends T> objects, final HashMap<String, Class<? extends T>> types) {
        final HashMap<String, Integer> countList = new HashMap<>();

        for (final Map.Entry<String, Class<? extends T>> type : types.entrySet()) {
            int count = 0;
            for (final Object object : objects) {
                if (type.getValue().isInstance(object)) {
                    count++;
                }
            }
            countList.put(type.getKey(), count);
        }

        return countList;
    }

    private void hookedPlugins() {
        metrics.addCustomChart(new Metrics.DrilldownPie("hookedPlugins", () -> {
            final Map<String, Map<String, Integer>> map = new HashMap<>();
            for (final String hook : Compatibility.getHooked()) {
                final Plugin plug = Bukkit.getPluginManager().getPlugin(hook);
                final String hookVersion = plug == null ? "unknown" : plug.getDescription().getVersion();

                final Map<String, Integer> entry = new HashMap<>();
                entry.put(hookVersion, 1);
                map.put(hook, entry);
            }
            return map;
        }));
    }
}
