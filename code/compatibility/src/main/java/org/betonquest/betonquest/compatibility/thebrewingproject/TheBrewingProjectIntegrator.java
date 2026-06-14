package org.betonquest.betonquest.compatibility.thebrewingproject;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.compatibility.thebrewingproject.action.DrunkenEventActionFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.condition.ModifierConditionFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.item.BrewItemFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.item.BrewItemSerializer;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewAgeObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewConsumeObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewCookObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewDistillObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewMixObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.BrewTransferObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.DrunkenEventObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.StructureDestroyObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The brewing project integration.
 */
public class TheBrewingProjectIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of TheBrewingProject.
     */
    public static final String REQUIRED_VERSION = "3.2.0";

    /**
     * Create a new tbp integrator.
     */
    public TheBrewingProjectIntegrator() {
        super();
    }

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
        objective("brew_consume", new BrewConsumeObjectiveFactory());
        objective("brew_age", new BrewAgeObjectiveFactory(tbpApi));
        objective("brew_cook", new BrewCookObjectiveFactory(tbpApi));
        objective("brew_mix", new BrewMixObjectiveFactory(tbpApi));
        objective("brew_distill", new BrewDistillObjectiveFactory(tbpApi));
        objective("brew_transfer", new BrewTransferObjectiveFactory());
        objective("drunken_event", new DrunkenEventObjectiveFactory());
        objective("structure_destroy", new StructureDestroyObjectiveFactory());
        registerFeatures(api);
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
