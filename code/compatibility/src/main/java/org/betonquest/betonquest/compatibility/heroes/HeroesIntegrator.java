package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.heroes.action.HeroesExperienceActionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of Heroes.
     */
    public static final String REQUIRED_VERSION = "7.3.0";

    /**
     * Creates a new Integrator.
     */
    public HeroesIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final CharacterManager characterManager = Heroes.getInstance().getCharacterManager();
        final HeroClassManager classManager = Heroes.getInstance().getClassManager();

        playerCondition("attribute", new HeroesAttributeConditionFactory(characterManager));
        playerCondition("class", new HeroesClassConditionFactory(characterManager, classManager));
        playerCondition("skill", new HeroesSkillConditionFactory(characterManager));

        playerAction("exp", new HeroesExperienceActionFactory(characterManager));

        registerFeatures(api, "heroes");

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
