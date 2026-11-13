package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;

/**
 * Renames the "particle" action of the EffectLib integration to "el_particle".
 */
public class EffectLibParticleRename implements QuestMigration {

    /**
     * Empty default constructor.
     */
    public EffectLibParticleRename() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "actions", "particle", "el_particle");
    }
}
