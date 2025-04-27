package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreClassConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreProfessionLevelConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributePointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributeReallocationPointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassExperienceEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassPointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreProfessionExperienceEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreSkillPointsEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreBreakCustomBlockObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreChangeClassObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreProfessionObjectiveFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrator for MMO CORE.
 */
public class MMOCoreIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public MMOCoreIntegrator() {
    }

    @Override
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoclass", new MMOCoreClassConditionFactory(data));
        conditionTypes.register("mmoattribute", new MMOCoreAttributeConditionFactory(data));
        conditionTypes.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory(data));

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoprofessionlevelup", new MMOCoreProfessionObjectiveFactory());
        objectiveTypes.register("mmochangeclass", new MMOCoreChangeClassObjectiveFactory());
        objectiveTypes.register("mmocorebreakblock", new MMOCoreBreakCustomBlockObjectiveFactory());

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoclassexperience", new MMOCoreClassExperienceEventFactory(data));
        eventTypes.register("mmoprofessionexperience", new MMOCoreProfessionExperienceEventFactory(data));
        eventTypes.register("mmocoreclasspoints", new MMOCoreClassPointsEventFactory(data));
        eventTypes.register("mmocoreattributepoints", new MMOCoreAttributePointsEventFactory(data));
        eventTypes.register("mmocoreattributereallocationpoints", new MMOCoreAttributeReallocationPointsEventFactory(data));
        eventTypes.register("mmocoreskillpoints", new MMOCoreSkillPointsEventFactory(data));
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
