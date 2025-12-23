package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
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
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("mmoclass", new MMOCoreClassConditionFactory());
        conditionRegistry.register("mmoattribute", new MMOCoreAttributeConditionFactory());
        conditionRegistry.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory());

        final FeatureTypeRegistry<Objective> objectiveRegistry = questRegistries.objective();
        objectiveRegistry.register("mmoprofessionlevelup", new MMOCoreProfessionObjectiveFactory());
        objectiveRegistry.register("mmochangeclass", new MMOCoreChangeClassObjectiveFactory());
        objectiveRegistry.register("mmocorebreakblock", new MMOCoreBreakCustomBlockObjectiveFactory());

        final EventRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("mmoclassexperience", new MMOCoreClassExperienceEventFactory(data));
        eventRegistry.register("mmoprofessionexperience", new MMOCoreProfessionExperienceEventFactory(data));
        eventRegistry.register("mmocoreclasspoints", new MMOCoreClassPointsEventFactory(data));
        eventRegistry.register("mmocoreattributepoints", new MMOCoreAttributePointsEventFactory(data));
        eventRegistry.register("mmocoreattributereallocationpoints", new MMOCoreAttributeReallocationPointsEventFactory(data));
        eventRegistry.register("mmocoreskillpoints", new MMOCoreSkillPointsEventFactory(data));
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
