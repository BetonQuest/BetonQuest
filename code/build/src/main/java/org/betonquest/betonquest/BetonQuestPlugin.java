package org.betonquest.betonquest;

import org.betonquest.betonquest.compatibility.BundledCompatibility;

/**
 * Represents Multi Module Entry Point for BetonQuest plugin.
 */
public class BetonQuestPlugin extends BetonQuest {

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public BetonQuestPlugin() {
        super();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BundledCompatibility(getCompatibility(), this).registerCompatiblePlugins();
        getCompatibility().init();
    }
}
