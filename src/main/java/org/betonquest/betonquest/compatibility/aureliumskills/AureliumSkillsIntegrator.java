package org.betonquest.betonquest.compatibility.aureliumskills;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;

/**
 * Integrator for AureliumSkills.
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody", "PMD.CommentRequired"})
public class AureliumSkillsIntegrator implements Integrator {

    @Override
    public void hook() throws HookException {
        BetonQuest.getInstance().registerConditions("aureliumskillslevel", AureliumSkillsLevelCondition.class);
        BetonQuest.getInstance().registerConditions("aureliumstatslevel", AureliumSkillsStatsCondition.class);

        BetonQuest.getInstance().registerEvents("aureliumskillsxp", AureliumSkillsExperienceEvent.class);

    }

    @Override
    public void reload() {
    }

    @Override
    public void close() {
    }
}
