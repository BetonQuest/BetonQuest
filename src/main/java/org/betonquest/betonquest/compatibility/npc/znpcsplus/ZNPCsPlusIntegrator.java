package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.bukkit.Bukkit;

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
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final NpcTypeRegistry npcTypes = betonQuest.getFeatureRegistries().npc();
        final ProfileProvider profileProvider = betonQuest.getProfileProvider();
        Bukkit.getPluginManager().registerEvents(new ZNPCsPlusCatcher(profileProvider, npcTypes), betonQuest);
        final ZNPCsPlusHider hider = new ZNPCsPlusHider(betonQuest.getFeatureAPI().getNpcHider());
        Bukkit.getPluginManager().registerEvents(hider, betonQuest);
        npcTypes.register(PREFIX, new ZNPCsPlusFactory(NpcApiProvider.get().getNpcRegistry()));
        npcTypes.registerIdentifier(new ZNPCsPlusIdentifier(PREFIX));
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
