package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the EffectLib migration.
 */
public class EffectLib implements Migration {

    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new effect_lib migrator.
     *
     * @param provider The config provider
     */
    public EffectLib(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection npcEffects = config.getConfigurationSection("npc_effects");
            if (npcEffects != null) {
                config.set("effectlib", npcEffects);
                migrateSection(npcEffects);
                config.set("npc_effects", null);
                config.save(file);
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
