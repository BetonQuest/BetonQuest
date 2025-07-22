package org.betonquest.betonquest.compatibility.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;

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
    public void hook() {
        final WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final File folder = new File(worldEdit.getDataFolder(), "schematics");
        final BetonQuest plugin = BetonQuest.getInstance();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        plugin.getQuestRegistries().event().registerCombined("paste", new PasteSchematicEventFactory(folder, data));
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
