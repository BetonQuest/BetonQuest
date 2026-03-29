package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.NpcApiProvider;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;

/**
 * Integrator implementation for the
 * <a href="https://www.spigotmc.org/resources/znpcsplus.109380/">ZNPCsPlus plugin</a>.
 */
public class ZNPCsPlusIntegrator implements Integration {

    /**
     * The minimum required version of ZNPCsPlus.
     */
    public static final String REQUIRED_VERSION = "2.1.0-SNAPSHOT";

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
    public void enable(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new ZNPCsPlusCatcher(profileProvider, npcRegistry));
        api.bukkit().registerEvents(new ZNPCsPlusHider(api.npcs().manager()));
        npcRegistry.register(PREFIX, new ZNPCsPlusFactory(NpcApiProvider.get().getNpcRegistry()));
        npcRegistry.registerIdentifier(new ZNPCsPlusIdentifier(PREFIX));
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
