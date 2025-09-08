package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.event.HeroesExperienceEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
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
        final CharacterManager characterManager = Heroes.getInstance().getCharacterManager();
        final HeroClassManager classManager = Heroes.getInstance().getClassManager();

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("heroesattribute", new HeroesAttributeConditionFactory(loggerFactory, data, characterManager));
        conditionRegistry.register("heroesclass", new HeroesClassConditionFactory(loggerFactory, data, characterManager, classManager));
        conditionRegistry.register("heroesskill", new HeroesSkillConditionFactory(loggerFactory, data, characterManager));

        questRegistries.event().register("heroesexp", new HeroesExperienceEventFactory(loggerFactory, data, characterManager));

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
