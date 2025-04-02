package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;

/**
 * Handles the EffectLib migration.
 */
public class EffectLib implements QuestMigration {

    /**
     * Creates a new effect_lib migrator.
     */
    public EffectLib() {
    }

    @Override
    public void migrate(final Quest quest) throws IOException {
        for (final FileConfigAccessor config : quest.getConfigAccessors()) {
            final ConfigurationSection npcEffects = config.getConfigurationSection("npc_effects");
            if (npcEffects != null) {
                config.set("effectlib", npcEffects);
                migrateSection(npcEffects);
                config.set("npc_effects", null);
                config.save();
            }
        }
    }

    private void migrateSection(final ConfigurationSection npcEffects) {
        final int checkInterval = npcEffects.getInt("check_interval");
        npcEffects.set("check_interval", null);
        npcEffects.set("disabled", null);
        npcEffects.getKeys(false).forEach(key -> {
            final ConfigurationSection subConfig = npcEffects.getConfigurationSection(key);
            if (subConfig == null) {
                return;
            }
            if (checkInterval != 0) {
                subConfig.set("checkinterval", checkInterval);
            }
            final int newPitch = subConfig.getInt("pitch") - 90;
            if (newPitch == 0) {
                subConfig.set("pitch", null);
            } else {
                subConfig.set("pitch", newPitch);
            }
        });
    }
}
