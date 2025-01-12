package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;

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
        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("mmoclass", MMOCoreClassCondition.class);
        conditionTypes.register("mmoattribute", MMOCoreAttributeCondition.class);
        conditionTypes.register("mmoprofession", MMOCoreProfessionLevelCondition.class);

        plugin.registerObjectives("mmoprofessionlevelup", MMOCoreProfessionObjective.class);
        plugin.registerObjectives("mmochangeclass", MMOCoreChangeClassObjective.class);
        plugin.registerObjectives("mmocorebreakblock", MMOCoreBreakCustomBlockObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
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
