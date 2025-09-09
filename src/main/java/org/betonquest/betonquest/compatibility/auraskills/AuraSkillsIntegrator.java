package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsLevelConditionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsStatsConditionFactory;
import org.betonquest.betonquest.compatibility.auraskills.event.AuraSkillsExperienceEventFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrator for <a href="https://github.com/Archy-X/AuraSkills">AuraSkills</a>.
 */
public class AuraSkillsIntegrator implements Integrator {
    /**
     * The {@link BetonQuest} plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public AuraSkillsIntegrator() {
        this.plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final AuraSkillsApi auraSkillsApi = AuraSkillsApi.get();
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();

        questRegistries.event().register("auraskillsxp", new AuraSkillsExperienceEventFactory(auraSkillsApi, data));

        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("auraskillslevel", new AuraSkillsLevelConditionFactory(auraSkillsApi, data));
        conditionTypes.register("auraskillsstatslevel", new AuraSkillsStatsConditionFactory(auraSkillsApi, data));
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
