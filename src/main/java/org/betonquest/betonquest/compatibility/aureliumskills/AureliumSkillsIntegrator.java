package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import lombok.Getter;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for AureliumSkills.
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody", "PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsIntegrator implements Integrator {

    @Getter
    private static AureliumSkills aureliumPlugin;

    @Override
    public void hook() throws HookException {
        final Plugin probablyAurelium = Bukkit.getPluginManager().getPlugin("AureliumSkills");
        try {
            aureliumPlugin = (AureliumSkills) probablyAurelium;
        } catch (final ClassCastException exception) {
            throw new HookException(probablyAurelium, "AureliumSkills wasn't able to be hooked due to: " + exception);
        }

        if (aureliumPlugin == null) {
            throw new HookException(null, "AureliumSkills wasn't able to be hooked: The plugin instance is null.");
        }

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
