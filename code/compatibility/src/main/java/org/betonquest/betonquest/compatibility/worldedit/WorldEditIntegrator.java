package org.betonquest.betonquest.compatibility.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Integrator for WorldEdit.
 */
public class WorldEditIntegrator implements Integration {

    /**
     * The minimum required version of WorldEdit.
     */
    public static final String WE_REQUIRED_VERSION = "7.3.19";

    /**
     * The minimum required version of FastAsyncWorldEdit.
     */
    public static final String FAWE_REQUIRED_VERSION = "2.10.0";

    /**
     * The name of the WorldEdit plugin.
     */
    private static final String WE_NAME = "WorldEdit";

    /**
     * The name of the FastAsyncWorldEdit plugin.
     */
    private static final String FAWE_NAME = "FastAsyncWorldEdit";

    /**
     * The default constructor.
     */
    public WorldEditIntegrator() {

    }

    /**
     * Provides the valid policies of the 'WorldEdit' plugin.
     *
     * @return policies to check whether the correct version is installed or not
     */
    public static Policy[] getPolicies() {
        final Policy weVersionPolicy = Policies.minimalPluginVersion(WE_NAME, VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, WE_REQUIRED_VERSION));
        final Policy faweVersionPolicy = Policies.minimalPluginVersion(FAWE_NAME, VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, FAWE_REQUIRED_VERSION));
        return new Policy[]{
                Policies.simpleCondition(() -> weVersionPolicy.validate() || faweVersionPolicy.validate(),
                        "WorldEdit version '%s' or above is required (or FastAsyncWorldEdit '%s' or above).".formatted(WE_REQUIRED_VERSION, FAWE_REQUIRED_VERSION))
        };
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final File folder = new File(worldEdit.getDataFolder(), "schematics");
        api.actions().registry().registerCombined("paste", new PasteSchematicActionFactory(folder));
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
