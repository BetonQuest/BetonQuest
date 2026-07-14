package org.betonquest.betonquest.compatibility.thebrewingproject;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.policy.Policy;
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
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.StructureCreateObjectiveFactory;
import org.betonquest.betonquest.compatibility.thebrewingproject.objective.StructureDestroyObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The TheBrewingProject integrator.
 */
public class TheBrewingProjectIntegrator extends IntegrationTemplate {

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
        playerCondition("modifier", new ModifierConditionFactory(tbpApi));
        playerAction("event", new DrunkenEventActionFactory(tbpApi));
        item("brew", new BrewItemFactory(tbpApi), new BrewItemSerializer(tbpApi.getBrewManager()));
        objective("consume", new BrewConsumeObjectiveFactory(tbpApi));
        objective("age", new BrewAgeObjectiveFactory(tbpApi));
        objective("cook", new BrewCookObjectiveFactory(tbpApi));
        objective("mix", new BrewMixObjectiveFactory(tbpApi));
        objective("distill", new BrewDistillObjectiveFactory(tbpApi));
        objective("transfer", new BrewTransferObjectiveFactory(tbpApi));
        objective("event", new DrunkenEventObjectiveFactory());
        objective("structure_destroy", new StructureDestroyObjectiveFactory());
        objective("structure_create", new StructureCreateObjectiveFactory());
        registerFeatures(api, "tbp_");
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }

    /**
     * The requirements for the right version of TheBrewingProject to be run.
     *
     * @return all policies for TheBrewingProject to run
     */
    public static Policy[] policies() {
        return new Policy[]{
                Policies.requireClass("dev.jsinco.brewery.api.ingredient.ResolvedIngredientManager")
        };
    }
}
