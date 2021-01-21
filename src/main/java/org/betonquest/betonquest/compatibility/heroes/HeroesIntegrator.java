package org.betonquest.betonquest.compatibility.heroes;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class HeroesIntegrator implements Integrator {

    private final BetonQuest plugin;

    public HeroesIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("heroesclass", HeroesClassCondition.class);
        plugin.registerConditions("heroesskill", HeroesSkillCondition.class);
        plugin.registerEvents("heroesexp", HeroesExperienceEvent.class);
        new HeroesMobKillListener();
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
