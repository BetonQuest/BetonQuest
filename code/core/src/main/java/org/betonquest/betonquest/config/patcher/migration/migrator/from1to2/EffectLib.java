package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

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
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        final String oldPath = "npc_effects";
        final ConfigurationSection npcEffects = config.getConfigurationSection(oldPath);
        final ConfigurationSection source = config.getSourceConfigurationSection(oldPath);
        if (npcEffects == null || source == null) {
            return;
        }
        migrateSection(npcEffects);
        config.set("effectlib", npcEffects);
        config.set(oldPath, null);
        config.associateWith(source);
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
