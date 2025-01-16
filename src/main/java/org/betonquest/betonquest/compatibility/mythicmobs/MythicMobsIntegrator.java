package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mythicmobs.conditions.MythicMobDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.events.MythicSpawnMobEventFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for MythicMobs.
 */
public class MythicMobsIntegrator implements Integrator {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public MythicMobsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        validateVersion();

        final BukkitAPIHelper apiHelper = new BukkitAPIHelper();

        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.getConditionTypes().register("mythicmobdistance", new MythicMobDistanceConditionFactory(apiHelper, data));
        plugin.registerObjectives("mmobkill", MythicMobKillObjective.class);
        questRegistries.getEventTypes().registerCombined("mspawnmob", new MythicSpawnMobEventFactory(apiHelper, data));
    }

    /**
     * Aborts the hooking process if the installed version of MythicMobs is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of MythicMobs is < 5.0.0.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        final String versionWithCommit = mythicMobs.getDescription().getVersion();
        final String[] parts = versionWithCommit.split("-");
        final Version mythicMobsVersion = new Version(parts[0]);
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "-");
        if (comparator.isOtherNewerThanCurrent(mythicMobsVersion, new Version("5.0.0"))) {
            throw new UnsupportedVersionException(mythicMobs, "5.0.0+");
        }
    }

    @Override
    public void postHook() {
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            MythicHider.start();
        }
    }

    @Override
    public void reload() {
        if (MythicHider.getInstance() != null) {
            MythicHider.start();
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
