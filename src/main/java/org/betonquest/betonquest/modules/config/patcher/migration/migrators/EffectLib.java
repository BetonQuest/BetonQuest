package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handels the effect_lib migration.
 */
public class EffectLib implements Migrator {

    /**
     * The configs to migrate.
     */
    private final Map<File, YamlConfiguration> configs;

    /**
     * Creates a new effect_lib migrator.
     *
     * @param configs The configs to migrate.
     */
    public EffectLib(final Map<File, YamlConfiguration> configs) {
        this.configs = configs;
    }

    @Override
    public boolean needMigration() {
        return configs.values().stream().anyMatch(config -> config.contains("npc_effects"));
    }

    @Override
    public void migrate() throws IOException {
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();

            final ConfigurationSection npcEffects = config.getConfigurationSection("npc_effects ");
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
            final YamlConfiguration subConfig = configs.get(key);
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
