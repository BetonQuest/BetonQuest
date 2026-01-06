package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
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
    public void hook(final BetonQuestApi api) {
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("mmoclass", new MMOCoreClassConditionFactory());
        conditionRegistry.register("mmoattribute", new MMOCoreAttributeConditionFactory());
        conditionRegistry.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory());

        final ObjectiveRegistry objectiveRegistry = questRegistries.objective();
        objectiveRegistry.register("mmoprofessionlevelup", new MMOCoreProfessionObjectiveFactory());
        objectiveRegistry.register("mmochangeclass", new MMOCoreChangeClassObjectiveFactory());
        objectiveRegistry.register("mmocorebreakblock", new MMOCoreBreakCustomBlockObjectiveFactory());

        final EventRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("mmoclassexperience", new MMOCoreClassExperienceEventFactory());
        eventRegistry.register("mmoprofessionexperience", new MMOCoreProfessionExperienceEventFactory());
        eventRegistry.register("mmocoreclasspoints", new MMOCoreClassPointsEventFactory());
        eventRegistry.register("mmocoreattributepoints", new MMOCoreAttributePointsEventFactory());
        eventRegistry.register("mmocoreattributereallocationpoints", new MMOCoreAttributeReallocationPointsEventFactory());
        eventRegistry.register("mmocoreskillpoints", new MMOCoreSkillPointsEventFactory());
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
