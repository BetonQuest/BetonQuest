package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class WorldEditIntegrator implements Integrator {

    private final BetonQuest plugin;

    public WorldEditIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("paste", PasteSchematicEvent.class);
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
