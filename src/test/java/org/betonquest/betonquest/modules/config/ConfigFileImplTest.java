package org.betonquest.betonquest.modules.config;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BetonQuestLoggerService.class)
public class ConfigFileImplTest {

    final static File CONFIG = new File("src/test/resources/modules.config/config.yml");
    final static File PATCH = new File("src/test/resources/modules.config/config.patch.yml");

    @Test
    public void testBackup(@TempDir final File workDir) throws IOException, InvalidConfigurationException {
        final File config = new File(workDir, "config.yml");
        final File patch = new File(workDir, "config.patch.yml");
        FileUtils.copyFile(CONFIG, config);
        FileUtils.copyFile(PATCH, patch);

        final JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
        Mockito.when(plugin.getResource("config.yml")).thenReturn(new FileInputStream(config));
        Mockito.when(plugin.getResource("config.patch.yml")).thenReturn(new FileInputStream(patch));
        ConfigurationFile.create(config, plugin, "config.yml");

        final File backup = new File(config.getParentFile(), "backups" + File.separator + "1.12.1-CONFIG-1.zip");
        assertTrue(backup.exists());

    }
}
