package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integrator {
    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "FancyNpcs";

    /**
     * The empty default Constructor.
     */
    public FancyNpcsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final NpcRegistry npcRegistry = api.getFeatureRegistries().npc();
        final ProfileProvider profileProvider = betonQuest.getProfileProvider();
        Bukkit.getPluginManager().registerEvents(new FancyCatcher(profileProvider, npcRegistry), betonQuest);
        final FancyHider hider = new FancyHider(betonQuest.getFeatureApi().getNpcHider());
        Bukkit.getPluginManager().registerEvents(hider, betonQuest);
        npcRegistry.register(PREFIX, new FancyFactory());
        npcRegistry.registerIdentifier(new FancyIdentifier(PREFIX));
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
