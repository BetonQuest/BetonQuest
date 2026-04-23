package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.plugin.Plugin;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integration {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "FancyNpcs";

    /**
     * The minimum required version of FancyNpcs.
     */
    public static final String REQUIRED_VERSION = "2.4.1";

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

    /**
     * The empty default Constructor.
     *
     * @param plugin the plugin instance to run tasks on
     */
    public FancyNpcsIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks for the valid version range of the 'FancyHolograms' plugin.
     *
     * @return whether the correct version is installed or not
     */
    public static Policy[] getPolicies() {
        return Policies.pluginVersionRange(PREFIX,
                VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, REQUIRED_VERSION),
                VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "2.999"));
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new FancyCatcher(plugin, profileProvider, npcRegistry));
        api.bukkit().registerEvents(new FancyHider(plugin, api.npcs().manager()));
        npcRegistry.register(PREFIX, new FancyFactory(plugin));
        npcRegistry.registerIdentifier(new FancyIdentifier(PREFIX));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
