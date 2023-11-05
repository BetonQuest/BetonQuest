package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

/**
 * Handels the npc_holograms migration.
 */
public class NpcHolograms implements Migrator {

    /**
     * The configs to migrate.
     */
    private final Map<File, YamlConfiguration> configs;

    /**
     * Creates a new npc_holograms migrator.
     *
     * @param configs The configs to migrate.
     */
    public NpcHolograms(final Map<File, YamlConfiguration> configs) {
        this.configs = configs;
    }

    @Override
    public boolean needMigration() {
        return configs.values().stream().anyMatch(config -> {
                    if (config.contains("npc_holograms.follow")
                            | config.contains("npc_holograms.check_interval")
                            | config.contains("holograms.check_interval")) {
                        return true;
                    }
                    final ConfigurationSection npcHolograms = config.getConfigurationSection("npc_holograms");
                    return npcHolograms != null && npcHolograms.getValues(false).values().stream()
                            .filter(subConfig -> subConfig instanceof ConfigurationSection)
                            .map(subConfig -> (ConfigurationSection) subConfig)
                            .anyMatch(subConfig -> "0;3;0".equals(subConfig.getString("vector")));
                }
        );
    }

    @Override
    public void migrate() {
        configs.forEach((file, config) -> {
            migrateFollow(config.getConfigurationSection("npc_holograms"));
            migrateCheckInterval(config.getConfigurationSection("npc_holograms"));
            migrateCheckInterval(config.getConfigurationSection("holograms"));
            migrateVector(config.getConfigurationSection("npc_holograms"));
            try {
                config.save(file);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void migrateFollow(final ConfigurationSection npcHolograms) {
        if (npcHolograms == null) {
            return;
        }
        final boolean follow = npcHolograms.getBoolean("follow", false);
        npcHolograms.set("follow", null);
        if (!follow) {
            return;
        }
        npcHolograms.getValues(false).values().stream()
                .filter(subConfig -> subConfig instanceof ConfigurationSection)
                .map(subConfig -> (ConfigurationSection) subConfig)
                .forEach(subConfig -> subConfig.set("follow", true));
    }

    private void migrateCheckInterval(final ConfigurationSection npcHolograms) {
        if (npcHolograms == null) {
            return;
        }
        final int checkInterval = npcHolograms.getInt("check_interval", 200);
        npcHolograms.set("check_interval", null);
        if (checkInterval == 200) {
            return;
        }
        npcHolograms.getValues(false).values().stream()
                .filter(subConfig -> subConfig instanceof ConfigurationSection)
                .map(subConfig -> (ConfigurationSection) subConfig)
                .forEach(subConfig -> subConfig.set("check_interval", checkInterval));
    }

    private void migrateVector(final ConfigurationSection npcHolograms) {
        if (npcHolograms == null) {
            return;
        }
        npcHolograms.getValues(false).values().stream()
                .filter(subConfig -> subConfig instanceof ConfigurationSection)
                .map(subConfig -> (ConfigurationSection) subConfig)
                .forEach(this::migrateVectorValue);
    }

    private void migrateVectorValue(final ConfigurationSection subConfig) {
        final String vector = subConfig.getString("vector", "0;3;0");
        if ("0;3;0".equals(vector)) {
            subConfig.set("vector", null);
        } else {
            final String[] split = vector.split(";");
            subConfig.set("vector", split[0] +
                    (Integer.parseInt(split[1]) - 3) +
                    split[2]);
        }
    }
}
