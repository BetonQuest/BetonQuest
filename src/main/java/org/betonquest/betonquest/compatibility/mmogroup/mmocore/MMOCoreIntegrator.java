package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MMOCoreIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        MMOCoreUtils.loadMMOCoreAttributeConfig();

        plugin.registerConditions("mmoclass", MMOCoreClassCondition.class);
        plugin.registerConditions("mmoattribute", MMOCoreAttributeCondition.class);
        plugin.registerConditions("mmoprofession", MMOCoreProfessionLevelCondition.class);

        plugin.registerObjectives("mmoprofessionlevelup", MMOCoreProfessionObjective.class);
        plugin.registerObjectives("mmocorecastskill", MMOCoreCastSkillObjective.class);
        plugin.registerObjectives("mmocorebreakblock", MMOCoreBreakCustomBlockObjective.class);

        plugin.registerEvents("mmoclassexperience", MMOCoreClassExperienceEvent.class);
        plugin.registerEvents("mmoprofessionexperience", MMOCoreProfessionExperienceEvent.class);
        plugin.registerEvents("mmocoreclasspoints", MMOCoreClassPointsEvent.class);
        plugin.registerEvents("mmocoreattributepoints", MMOCoreAttributePointsEvent.class);
        plugin.registerEvents("mmocoreattributereallocationpoints", MMOCoreAttributeReallocationPointsEvent.class);
        plugin.registerEvents("mmocoreskillpoints", MMOCoreSkillPointsEvent.class);
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
