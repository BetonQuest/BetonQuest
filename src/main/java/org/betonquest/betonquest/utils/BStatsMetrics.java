package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.id.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class BStatsMetrics {
    private final static int METRICS_ID = 551;

    private final Metrics metrics;
    private final JavaPlugin plugin;

    public BStatsMetrics(final JavaPlugin plugin,
                         final Map<ConditionID, Condition> conditions,
                         final Map<EventID, QuestEvent> events,
                         final Map<ObjectiveID, Objective> objectives,
                         final Map<VariableID, Variable> variables,
                         final Map<String, Class<? extends Condition>> conditionTypes,
                         final Map<String, Class<? extends QuestEvent>> eventTypes,
                         final Map<String, Class<? extends Objective>> objectiveTypes,
                         final Map<String, Class<? extends Variable>> variableTypes) {
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

    private <T> void listUsage(final String bStatsId, final Map<? extends ID, ? extends T> objects, final Map<String, Class<? extends T>> types) {
        metrics.addCustomChart(new Metrics.AdvancedPie(bStatsId + "Count", () -> countUsageClasses(objects.values(), types)));
        metrics.addCustomChart(new Metrics.AdvancedPie(bStatsId + "Enabled", () -> {
            final Map<String, Integer> enabled = new HashMap<>();
            final Map<String, Integer> usage = countUsageClasses(objects.values(), types);

            for (final Map.Entry<String, Integer> use : usage.entrySet()) {
                enabled.put(use.getKey(), 1);
            }

            return enabled;
        }
        ));
    }

    private <T> Map<String, Integer> countUsageClasses(final Collection<? extends T> objects, final Map<String, Class<? extends T>> types) {
        final Map<String, Integer> countList = new HashMap<>();

        for (final Map.Entry<String, Class<? extends T>> type : types.entrySet()) {
            int count = 0;
            for (final Object object : objects) {
                if (type.getValue().isInstance(object)) {
                    count++;
                }
            }
            if (count > 0) {
                countList.put(type.getKey(), count);
            }
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
