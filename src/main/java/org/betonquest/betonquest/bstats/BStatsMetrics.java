package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ID;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * bStats metrics sending class. It implements BetonQuest's custom charts.
 */
public class BStatsMetrics {
    /**
     * The metrics instance to send metrics.
     */
    private final Metrics metrics;

    /**
     * The plugin that metrics are sent for.
     */
    private final Plugin plugin;

    /**
     * Create a BStatsMetrics instance.
     *
     * @param plugin           plugin to send metrics for
     * @param metrics          metrics instance to use
     * @param metricsSuppliers instruction metrics suppliers to query for metrics
     */
    public BStatsMetrics(final Plugin plugin, final Metrics metrics, final Map<String, InstructionMetricsSupplier<? extends ID>> metricsSuppliers) {
        this.plugin = plugin;
        this.metrics = metrics;

        versionMcBq();
        metricsSuppliers.forEach(this::listUsage);
        hookedPlugins();
        installedPlugins();
    }

    private void versionMcBq() {
        final String versionPlugin = plugin.getDescription().getVersion();
        final String versionMc = plugin.getServer().getBukkitVersion().split("-")[0];

        metrics.addCustomChart(new DrilldownPie("versionMcBq", () -> getDrillDownPie(versionPlugin, versionMc)));
        metrics.addCustomChart(new DrilldownPie("versionBqMc", () -> getDrillDownPie(versionMc, versionPlugin)));
    }

    private Map<String, Map<String, Integer>> getDrillDownPie(final String value1, final String value2) {
        final Map<String, Map<String, Integer>> map = new HashMap<>();
        final Map<String, Integer> entry = new HashMap<>();
        entry.put(value1, 1);
        map.put(value2, entry);
        return map;
    }

    private void listUsage(final String bStatsId, final InstructionMetricsSupplier<? extends ID> instructionMetricsSupplier) {
        metrics.addCustomChart(new AdvancedPie(bStatsId + "Count", () -> countUsages(instructionMetricsSupplier)));
        metrics.addCustomChart(new AdvancedPie(bStatsId + "Enabled", () -> collectEnabled(instructionMetricsSupplier)));
    }

    private Map<String, Integer> collectEnabled(final InstructionMetricsSupplier<? extends ID> instructionMetricsSupplier) {
        final Map<String, Integer> enabled = new HashMap<>();
        countUsages(instructionMetricsSupplier).forEach((key, count) -> enabled.put(key, 1));
        return enabled;
    }

    private Map<String, Integer> countUsages(final InstructionMetricsSupplier<? extends ID> instructionMetricsSupplier) {
        final Set<String> validTypes = instructionMetricsSupplier.getTypes();
        return instructionMetricsSupplier.getIdentifiers().stream()
                .map(this::typeFromId)
                .filter(validTypes::contains)
                .collect(Collectors.toMap(Function.identity(), key -> 1, Integer::sum));
    }

    @Nullable
    private String typeFromId(final ID identifier) {
        try {
            return identifier.getInstruction().getPart(0);
        } catch (final QuestException ex) {
            // ignore broken instructions
            return null;
        }
    }

    private void hookedPlugins() {
        metrics.addCustomChart(new DrilldownPie("hookedPlugins", () -> {
            final Map<String, Map<String, Integer>> map = new HashMap<>();
            for (final String hook : Compatibility.getHooked()) {
                final Plugin plug = plugin.getServer().getPluginManager().getPlugin(hook);
                final String hookVersion = plug == null ? "unknown" : plug.getDescription().getVersion();

                final Map<String, Integer> entry = new HashMap<>();
                entry.put(hookVersion, 1);
                map.put(hook, entry);
            }
            return map;
        }));
    }

    private void installedPlugins() {
        metrics.addCustomChart(new DrilldownPie("installedPlugins", () -> {
            final Map<String, Map<String, Integer>> map = new HashMap<>();
            for (final Plugin plug : Bukkit.getPluginManager().getPlugins()) {
                if (plug instanceof BetonQuest) {
                    continue;
                }
                final String hookVersion = plug.getDescription().getVersion();

                final Map<String, Integer> entry = new HashMap<>();
                entry.put(hookVersion, 1);
                map.put(plug.getName() + " by " + plug.getDescription().getAuthors(), entry);
            }
            return map;
        }));
    }
}
