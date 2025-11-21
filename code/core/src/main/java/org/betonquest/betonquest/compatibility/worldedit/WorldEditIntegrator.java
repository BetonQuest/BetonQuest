package org.betonquest.betonquest.compatibility.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Integrator for WorldEdit.
 */
public class WorldEditIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public WorldEditIntegrator() {

    }

    @Override
    public void hook(final BetonQuestApi api) {
        final WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final File folder = new File(worldEdit.getDataFolder(), "schematics");
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        api.getQuestRegistries().event().registerCombined("paste", new PasteSchematicEventFactory(folder, data));
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
