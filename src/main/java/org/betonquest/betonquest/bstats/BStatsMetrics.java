package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ID;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.CommentRequired")
public class BStatsMetrics {
    private final static int METRICS_ID = 551;

    private final Metrics metrics;
    private final JavaPlugin plugin;

    public BStatsMetrics(final JavaPlugin plugin, final Map<String, InstructionMetricsSupplier<? extends ID>> metricsSuppliers) {
        this.plugin = plugin;
        metrics = new Metrics(plugin, METRICS_ID);

        versionMcBq();
        metricsSuppliers.forEach(this::listUsage);
        hookedPlugins();
    }

    private void versionMcBq() {
        final String versionPlugin = plugin.getDescription().getVersion();
        final String versionMc = plugin.getServer().getBukkitVersion().split("-")[0];

        metrics.addCustomChart(new DrilldownPie("versionMcBq", () -> getDrillDownPie(versionPlugin, versionMc)));
        metrics.addCustomChart(new DrilldownPie("versionBqMc", () -> getDrillDownPie(versionMc, versionPlugin)));
    }

    public Map<String, Map<String, Integer>> getDrillDownPie(final String value1, final String value2) {
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

    private String typeFromId(final ID identifier) {
        try {
            return identifier.generateInstruction().getPart(0);
        } catch (final InstructionParseException ex) {
            // ignore broken instructions
            return null;
        }
    }

    private void hookedPlugins() {
        metrics.addCustomChart(new DrilldownPie("hookedPlugins", () -> {
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
