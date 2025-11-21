package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Handles the aura_skills rename migration.
 */
public class AuraSkillsRename implements QuestMigration {

    /**
     * Creates a new aura_skills migrator.
     */
    public AuraSkillsRename() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "conditions", "aureliumskillslevel", "auraskillslevel");
        replaceStartValueInSection(config, "conditions", "aureliumstatslevel", "auraskillsstatslevel");
        replaceStartValueInSection(config, "events", "aureliumskillsxp", "auraskillsxp");
    }
}
