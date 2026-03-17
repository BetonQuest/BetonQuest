package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.heroes.action.HeroesExperienceActionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integration {

    /**
     * Creates a new Integrator.
     */
    public HeroesIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final CharacterManager characterManager = Heroes.getInstance().getCharacterManager();
        final HeroClassManager classManager = Heroes.getInstance().getClassManager();

        final ConditionRegistry conditionRegistry = api.conditions().registry();
        conditionRegistry.register("heroesattribute", new HeroesAttributeConditionFactory(characterManager));
        conditionRegistry.register("heroesclass", new HeroesClassConditionFactory(characterManager, classManager));
        conditionRegistry.register("heroesskill", new HeroesSkillConditionFactory(characterManager));

        api.actions().registry().register("heroesexp", new HeroesExperienceActionFactory(characterManager));

        api.bukkit().registerEvents(new HeroesMobKillListener(api.profiles()));
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
