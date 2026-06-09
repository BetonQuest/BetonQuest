package org.betonquest.betonquest.compatibility.tbp;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.tbp.action.DrunkenEventActionFactory;
import org.betonquest.betonquest.compatibility.tbp.condition.ModifierCondition;
import org.betonquest.betonquest.compatibility.tbp.item.TbpItemFactory;
import org.betonquest.betonquest.compatibility.tbp.item.TbpItemSerializer;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The brewing project integration.
 */
public class TbpIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of TheBrewingProject.
     */
    public static final String REQUIRED_VERSION = "3.2.0";

    @Override
    public void enable(final BetonQuestApi api) {
        final RegisteredServiceProvider<TheBrewingProjectApi> tbpProvider = Bukkit.getServicesManager().getRegistration(TheBrewingProjectApi.class);
        if (tbpProvider == null) {
            return;
        }
        final TheBrewingProjectApi tbpApi = tbpProvider.getProvider();
        playerCondition("drunken_modifier", new ModifierCondition(tbpApi));
        playerAction("drunken_event", new DrunkenEventActionFactory(tbpApi));
        item("brew", new TbpItemFactory(tbpApi), new TbpItemSerializer());

        registerFeatures(api);
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // NO-OP
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
