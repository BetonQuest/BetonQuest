package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the NpcHolograms migration.
 */
public class NpcHolograms implements Migration {

    /**
     * The npc_holograms string.
     */
    public static final String NPC_HOLOGRAMS = "npc_holograms";

    /**
     * The vector string.
     */
    public static final String VECTOR = "vector";

    /**
     * The default check interval.
     */
    public static final int DEFAULT_CHECK_INTERVAL = 200;

    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new npc_holograms migrator.
     *
     * @param provider The config provider
     */
    public NpcHolograms(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            if (config.contains(NPC_HOLOGRAMS + ".follow", false)
                    || config.contains(NPC_HOLOGRAMS + ".check_interval", false)
                    || config.contains("holograms.check_interval", false)) {
                migrateFollow(config.getConfigurationSection(NPC_HOLOGRAMS));
                migrateCheckInterval(config.getConfigurationSection(NPC_HOLOGRAMS));
                migrateCheckInterval(config.getConfigurationSection("holograms"));
                migrateVector(config.getConfigurationSection(NPC_HOLOGRAMS));
                config.save(file);
            }
        }
    }

    private void migrateFollow(@Nullable final ConfigurationSection npcHolograms) {
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

    private void migrateCheckInterval(@Nullable final ConfigurationSection npcHolograms) {
        if (npcHolograms == null) {
            return;
        }
        final int checkInterval = npcHolograms.getInt("check_interval", DEFAULT_CHECK_INTERVAL);
        npcHolograms.set("check_interval", null);
        if (checkInterval == DEFAULT_CHECK_INTERVAL) {
            return;
        }
        npcHolograms.getValues(false).values().stream()
                .filter(subConfig -> subConfig instanceof ConfigurationSection)
                .map(subConfig -> (ConfigurationSection) subConfig)
                .forEach(subConfig -> subConfig.set("check_interval", checkInterval));
    }

    private void migrateVector(@Nullable final ConfigurationSection npcHolograms) {
        if (npcHolograms == null) {
            return;
        }
        npcHolograms.getValues(false).values().stream()
                .filter(subConfig -> subConfig instanceof ConfigurationSection)
                .map(subConfig -> (ConfigurationSection) subConfig)
                .forEach(this::migrateVectorValue);
    }

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    private void migrateVectorValue(final ConfigurationSection subConfig) {
        final String oldVector = "0;3;0";
        final String vector = subConfig.getString(VECTOR, oldVector);
        if (oldVector.equals(vector)) {
            subConfig.set(VECTOR, null);
        } else {
            final String[] split = vector.split(";");
            String yVector;
            try {
                yVector = String.valueOf(Double.parseDouble(split[1]) - 3);
            } catch (final NumberFormatException e) {
                yVector = split[1];
            }
            subConfig.set(VECTOR, split[0] + ";" + yVector + ";" + split[2]);
        }
    }
}
