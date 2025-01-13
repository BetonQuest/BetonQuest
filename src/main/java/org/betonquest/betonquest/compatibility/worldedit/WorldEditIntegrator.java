package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

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
        BetonQuest.getInstance().getQuestRegistries().event().register("paste", PasteSchematicEvent.class);
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
