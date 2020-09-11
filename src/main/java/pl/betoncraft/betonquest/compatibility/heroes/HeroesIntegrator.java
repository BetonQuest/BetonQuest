package pl.betoncraft.betonquest.compatibility.heroes;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class HeroesIntegrator implements Integrator {

    private BetonQuest plugin;

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

    }

    @Override
    public void close() {

    }

}
