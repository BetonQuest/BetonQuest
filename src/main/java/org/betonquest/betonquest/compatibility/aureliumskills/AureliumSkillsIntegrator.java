package org.betonquest.betonquest.compatibility.aureliumskills;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;

/**
 * Integrator for AureliumSkills.
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody"})
public class AureliumSkillsIntegrator implements Integrator {

    @Override
    public void hook() throws HookException {
        BetonQuest.getInstance().registerConditions("aureliumskillslevel", AureliumSkillsLevelCondition.class);
    }

    @Override
    public void reload() {
    }

    @Override
    public void close() {
    }
}
