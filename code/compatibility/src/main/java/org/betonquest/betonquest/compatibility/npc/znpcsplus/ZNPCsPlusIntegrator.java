package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.lib.versioning.LegacyVersion;
import org.betonquest.betonquest.lib.versioning.UpdateStrategy;
import org.betonquest.betonquest.lib.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
     * The default Constructor.
     */
    public ZNPCsPlusIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        validateVersion();
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new ZNPCsPlusCatcher(profileProvider, npcRegistry));
        final ZNPCsPlusHider hider = new ZNPCsPlusHider(BetonQuest.getInstance().getComponentLoader().get(NpcProcessor.class).getNpcHider());
        api.bukkit().registerEvents(hider);
        npcRegistry.register(PREFIX, new ZNPCsPlusFactory(NpcApiProvider.get().getNpcRegistry()));
        npcRegistry.registerIdentifier(new ZNPCsPlusIdentifier(PREFIX));
    }

    private void validateVersion() throws UnsupportedVersionException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(PREFIX);
        final LegacyVersion currentVersion = new LegacyVersion(plugin.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "SNAPSHOT-");
        if (comparator.isOlderThan(new LegacyVersion("2.1.0-SNAPSHOT"), currentVersion)) {
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
