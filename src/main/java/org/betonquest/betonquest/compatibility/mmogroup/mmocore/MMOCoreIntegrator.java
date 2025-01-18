package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.ObjectiveTypeRegistry;

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
        MMOCoreUtils.loadMMOCoreAttributeConfig();

        final BetonQuest plugin = BetonQuest.getInstance();
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoclass", MMOCoreClassCondition.class);
        conditionTypes.register("mmoattribute", MMOCoreAttributeCondition.class);
        conditionTypes.register("mmoprofession", MMOCoreProfessionLevelCondition.class);

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoprofessionlevelup", MMOCoreProfessionObjective.class);
        objectiveTypes.register("mmochangeclass", MMOCoreChangeClassObjective.class);
        objectiveTypes.register("mmocorebreakblock", MMOCoreBreakCustomBlockObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoclassexperience", MMOCoreClassExperienceEvent.class);
        eventTypes.register("mmoprofessionexperience", MMOCoreProfessionExperienceEvent.class);
        eventTypes.register("mmocoreclasspoints", MMOCoreClassPointsEvent.class);
        eventTypes.register("mmocoreattributepoints", MMOCoreAttributePointsEvent.class);
        eventTypes.register("mmocoreattributereallocationpoints", MMOCoreAttributeReallocationPointsEvent.class);
        eventTypes.register("mmocoreskillpoints", MMOCoreSkillPointsEvent.class);
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
