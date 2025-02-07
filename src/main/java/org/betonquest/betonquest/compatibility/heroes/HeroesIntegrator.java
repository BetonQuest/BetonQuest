package org.betonquest.betonquest.compatibility.heroes;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.event.HeroesExperienceEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.bukkit.Server;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public HeroesIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("heroesattribute", new HeroesAttributeConditionFactory(loggerFactory, data));
        conditionTypes.register("heroesclass", new HeroesClassConditionFactory(loggerFactory, data));
        conditionTypes.register("heroesskill", new HeroesSkillConditionFactory(loggerFactory, data));

        questRegistries.event().register("heroesexp", new HeroesExperienceEventFactory(loggerFactory, data));

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
