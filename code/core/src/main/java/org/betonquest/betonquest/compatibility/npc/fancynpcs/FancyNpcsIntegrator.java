package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
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
    public void hook() throws HookException {
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final NpcTypeRegistry npcTypes = betonQuest.getFeatureRegistries().npc();
        final ProfileProvider profileProvider = betonQuest.getProfileProvider();
        Bukkit.getPluginManager().registerEvents(new FancyCatcher(profileProvider, npcTypes), betonQuest);
        final FancyHider hider = new FancyHider(betonQuest.getFeatureAPI().getNpcHider());
        Bukkit.getPluginManager().registerEvents(hider, betonQuest);
        npcTypes.register(PREFIX, new FancyFactory());
        npcTypes.registerIdentifier(new FancyIdentifier(PREFIX));
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
