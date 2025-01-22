package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the aura_skills rename migration.
 */
public class AuraSkillsRename implements Migration {
    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new aura_skills migrator.
     *
     * @param provider The config provider
     */
    public AuraSkillsRename(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final boolean cond1Replaced = replaceStartValueInSection(config, "conditions", "aureliumskillslevel", "auraskillslevel");
            final boolean cond2Replaced = replaceStartValueInSection(config, "conditions", "aureliumstatslevel", "auraskillsstatslevel");
            final boolean objReplaced = replaceStartValueInSection(config, "events", "aureliumskillsxp", "auraskillsxp");
            if (cond1Replaced || cond2Replaced || objReplaced) {
                config.save(file);
            }
        }
    }
}
