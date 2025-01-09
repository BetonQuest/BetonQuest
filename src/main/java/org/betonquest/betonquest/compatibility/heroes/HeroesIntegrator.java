package org.betonquest.betonquest.compatibility.heroes;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public HeroesIntegrator() {

    }

    @Override
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("heroesattribute", HeroesAttributeCondition.class);
        conditionTypes.register("heroesclass", HeroesClassCondition.class);
        conditionTypes.register("heroesskill", HeroesSkillCondition.class);
        questRegistries.getEventTypes().register("heroesexp", HeroesExperienceEvent.class);
        plugin.getServer().getPluginManager().registerEvents(new HeroesMobKillListener(), plugin);
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
