package org.betonquest.betonquest;

import org.betonquest.betonquest.compatibility.BundledCompatibility;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.mc_1_20_6.BundledMC_1_20_6;
import org.betonquest.betonquest.mc_1_21_4.BundledMC_1_21_4;

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
        try {
            super.onEnable();
        } catch (final IllegalStateException exception) {
            getLoggerFactory().create(this).error("Disabling BetonQuest due to an error: " + exception.getMessage(), exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        final Compatibility compatibility = getCompatibility();
        compatibility.registerVanilla("1.20.6", () -> new BundledMC_1_20_6(this));
        compatibility.registerVanilla("1.21.4", () -> new BundledMC_1_21_4(this));
        new BundledCompatibility(getLoggerFactory().create(BundledCompatibility.class),
                compatibility, this, this).registerCompatiblePlugins();
        compatibility.init();
        getServer().getPluginManager().registerEvents(compatibility, this);
    }
}
