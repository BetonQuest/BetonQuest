package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreAttributePointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreAttributeReallocationPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreClassExperienceActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreClassPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreProfessionExperienceActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.action.MMOCoreSkillPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreClassConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreProfessionLevelConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreBreakCustomBlockObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreChangeClassObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreProfessionObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for MMO CORE.
 */
public class MMOCoreIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of MMOCore.
     */
    public static final String REQUIRED_VERSION = "1.12-SNAPSHOT";

    /**
     * The default constructor.
     */
    public MMOCoreIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("class", new MMOCoreClassConditionFactory());
        playerCondition("attribute", new MMOCoreAttributeConditionFactory());
        playerCondition("profession", new MMOCoreProfessionLevelConditionFactory());

        objective("professionlevelup", new MMOCoreProfessionObjectiveFactory());
        objective("changeclass", new MMOCoreChangeClassObjectiveFactory());
        objective("corebreakblock", new MMOCoreBreakCustomBlockObjectiveFactory());

        playerAction("classexperience", new MMOCoreClassExperienceActionFactory());
        playerAction("professionexperience", new MMOCoreProfessionExperienceActionFactory());
        playerAction("coreclasspoints", new MMOCoreClassPointsActionFactory());
        playerAction("coreattributepoints", new MMOCoreAttributePointsActionFactory());
        playerAction("coreattributereallocationpoints", new MMOCoreAttributeReallocationPointsActionFactory());
        playerAction("coreskillpoints", new MMOCoreSkillPointsActionFactory());

        registerFeatures(api, "mmo");
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
