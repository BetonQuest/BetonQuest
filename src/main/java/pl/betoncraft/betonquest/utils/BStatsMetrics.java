package pl.betoncraft.betonquest.utils;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BStatsMetrics {
    private final static int METRICS_ID = 551;

    private final Metrics metrics;
    private final JavaPlugin plugin;

    public BStatsMetrics(final JavaPlugin plugin) {
        this.plugin = plugin;
        metrics = new Metrics(plugin, METRICS_ID);

        versionMcBq();
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
}
