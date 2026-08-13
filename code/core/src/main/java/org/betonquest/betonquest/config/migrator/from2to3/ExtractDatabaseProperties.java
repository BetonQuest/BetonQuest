package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Migrates the database configuration from the main configuration file to a separate {@code database.properties} file.
 */
public class ExtractDatabaseProperties implements Migration {

    /**
     * The BetonQuest config.yml file.
     */
    private final Path betonquestConfigYml;

    /**
     * The BetonQuest database.properties file.
     */
    private final Path betonquestDatabaseProperties;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the ExtractDatabaseProperties migration.
     *
     * @param plugin the plugin instance
     */
    public ExtractDatabaseProperties(final Plugin plugin) {
        this.plugin = plugin;

        final Path betonquest = plugin.getDataFolder().toPath();
        this.betonquestConfigYml = betonquest.resolve("config.yml");
        this.betonquestDatabaseProperties = betonquest.resolve("database.properties");
    }

    @Override
    public void migrate() throws IOException {
        if (Files.exists(betonquestDatabaseProperties)) {
            return;
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(betonquestConfigYml.toFile());
        final Properties props = getDatabaseConfigProperties(config);
        saveDatabaseProperties(props);
    }

    private Properties getDatabaseConfigProperties(final YamlConfiguration config) {
        final Properties props = new Properties();

        final String host = config.getString("mysql.host", "localhost");
        final String port = config.getString("mysql.port", "0000");
        final String base = config.getString("mysql.base", "betonquest");

        final String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false",
                host, port, base
        );

        props.setProperty("jdbcUrl", jdbcUrl);
        props.setProperty("username", config.getString("mysql.user", ""));
        props.setProperty("password", config.getString("mysql.pass", ""));
        return props;
    }

    private void saveDatabaseProperties(final Properties props) throws IOException {
        plugin.saveResource("database.properties", false);

        String content = Files.readString(betonquestDatabaseProperties);
        content = content.replaceAll("(?m)^jdbcUrl=.*$", "jdbcUrl=" + props.getProperty("jdbcUrl"));
        content = content.replaceAll("(?m)^username=.*$", "username=" + props.getProperty("username"));
        content = content.replaceAll("(?m)^password=.*$", "password=" + props.getProperty("password"));
        Files.writeString(betonquestDatabaseProperties, content);
    }
}
