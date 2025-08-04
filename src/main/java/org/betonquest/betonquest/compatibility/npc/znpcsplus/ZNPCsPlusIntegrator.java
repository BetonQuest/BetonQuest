package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
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
    public void hook() throws HookException {
        validateVersion();
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final NpcTypeRegistry npcTypes = betonQuest.getFeatureRegistries().npc();
        final ProfileProvider profileProvider = betonQuest.getProfileProvider();
        Bukkit.getPluginManager().registerEvents(new ZNPCsPlusCatcher(profileProvider, npcTypes), betonQuest);
        final ZNPCsPlusHider hider = new ZNPCsPlusHider(betonQuest.getFeatureApi().getNpcHider());
        Bukkit.getPluginManager().registerEvents(hider, betonQuest);
        npcTypes.register(PREFIX, new ZNPCsPlusFactory(NpcApiProvider.get().getNpcRegistry()));
        npcTypes.registerIdentifier(new ZNPCsPlusIdentifier(PREFIX));
    }

    private void validateVersion() throws UnsupportedVersionException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(PREFIX);
        final Version version = new Version(plugin.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "SNAPSHOT-");
        if (!comparator.isOtherNewerOrEqualThanCurrent(new Version("2.1.0-SNAPSHOT"), version)) {
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
