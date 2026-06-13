package org.betonquest.betonquest.compatibility.thebrewingproject;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.compatibility.thebrewingproject.action.DrunkenEventActionFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.condition.ModifierConditionFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.item.BrewItemFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.item.BrewItemSerializer;
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
    public void enable(final BetonQuestApi api) throws QuestException {
        final RegisteredServiceProvider<TheBrewingProjectApi> tbpProvider = Bukkit.getServicesManager().getRegistration(TheBrewingProjectApi.class);
        if (tbpProvider == null) {
            throw new QuestException("TheBrewingProject provider not present");
        }
        final TheBrewingProjectApi tbpApi = tbpProvider.getProvider();
        playerCondition("drunken_modifier", new ModifierConditionFactory(tbpApi));
        playerAction("drunken_event", new DrunkenEventActionFactory(tbpApi));
        item("brew", new BrewItemFactory(tbpApi), new BrewItemSerializer());

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
