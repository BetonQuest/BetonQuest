package org.betonquest.betonquest.compatibility.auraskills;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;

/**
 * Integrator for AuraSkills.
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody", "PMD.CommentRequired"})
public class AuraSkillsIntegrator implements Integrator {

    @Override
    public void hook() throws HookException {
        BetonQuest.getInstance().registerConditions("auraskillslevel", AuraSkillsLevelCondition.class);
        BetonQuest.getInstance().registerConditions("auraskillslevel", AuraSkillsStatsCondition.class);

        BetonQuest.getInstance().registerEvents("auraskillsxp", AuraSkillsExperienceEvent.class);
    }

    @Override
    public void reload() {
    }

    @Override
    public void close() {
    }
}
