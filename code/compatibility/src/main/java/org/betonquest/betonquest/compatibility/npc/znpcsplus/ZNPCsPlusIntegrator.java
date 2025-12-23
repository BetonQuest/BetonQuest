package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Integrator implementation for the
 * <a href="https://www.spigotmc.org/resources/znpcsplus.109380/">ZNPCsPlus plugin</a>.
 */
public class ZNPCsPlusIntegrator implements Integrator {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "ZNPCsPlus";

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * The default Constructor.
     *
     * @param plugin the plugin to register listener with
     */
    public ZNPCsPlusIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        validateVersion();
        final NpcRegistry npcRegistry = api.getFeatureRegistries().npc();
        final ProfileProvider profileProvider = api.getProfileProvider();
        final PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new ZNPCsPlusCatcher(profileProvider, npcRegistry), plugin);
        final ZNPCsPlusHider hider = new ZNPCsPlusHider(api.getFeatureApi().getNpcHider());
        manager.registerEvents(hider, plugin);
        npcRegistry.register(PREFIX, new ZNPCsPlusFactory(NpcApiProvider.get().getNpcRegistry()));
        npcRegistry.registerIdentifier(new ZNPCsPlusIdentifier(PREFIX));
    }

    private void validateVersion() throws UnsupportedVersionException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(PREFIX);
        final Version currentVersion = new Version(plugin.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "SNAPSHOT-");
        if (comparator.isOlderThan(new Version("2.1.0-SNAPSHOT"), currentVersion)) {
            throw new UnsupportedVersionException(plugin, "2.1.0-SNAPSHOT+");
        }
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
