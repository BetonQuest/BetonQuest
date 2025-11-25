package org.betonquest.betonquest;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.compatibility.BundledCompatibility;
import org.betonquest.betonquest.mc_1_20_6.BundledMC_1_20_6;

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
        if (PaperLib.isVersion(20, 6)) {
            new BundledMC_1_20_6(getLoggerFactory().create(BundledMC_1_20_6.class)).register(this);
        }
        new BundledCompatibility(getCompatibility(), this).registerCompatiblePlugins();
        getCompatibility().init();
    }
}
