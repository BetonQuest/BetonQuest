package org.betonquest.betonquest;

import org.betonquest.betonquest.compatibility.BundledCompatibility;
import org.betonquest.betonquest.mc_1_20_6.BundledMC_1_20_6;
import org.betonquest.betonquest.mc_1_21_4.BundledMC_1_21_4;
import org.betonquest.betonquest.versioning.MinecraftVersion;

/**
 * Represents Multi Module Entry Point for BetonQuest plugin.
 */
@SuppressWarnings("unused")
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
        final MinecraftVersion version = new MinecraftVersion(getServer());
        if (version.isCompatibleWith("1.20.6")) {
            new BundledMC_1_20_6(getLoggerFactory().create(BundledMC_1_20_6.class)).register(this);
        }
        if (version.isCompatibleWith("1.21.4")) {
            new BundledMC_1_21_4(getLoggerFactory().create(BundledMC_1_21_4.class)).register(this);
        }
        new BundledCompatibility(getCompatibility(), this).registerCompatiblePlugins();
        getCompatibility().init();
    }
}
