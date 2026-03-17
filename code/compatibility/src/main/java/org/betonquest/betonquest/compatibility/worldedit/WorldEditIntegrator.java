package org.betonquest.betonquest.compatibility.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Integrator for WorldEdit.
 */
public class WorldEditIntegrator implements Integration {

    /**
     * The default constructor.
     */
    public WorldEditIntegrator() {

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
