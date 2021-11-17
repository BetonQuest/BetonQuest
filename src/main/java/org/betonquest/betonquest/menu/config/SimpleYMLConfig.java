package org.betonquest.betonquest.menu.config;

import lombok.CustomLog;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@CustomLog
@SuppressWarnings("PMD.CommentRequired")
public abstract class SimpleYMLConfig extends SimpleYMLSection {

    protected final File file;

    public SimpleYMLConfig(final File file) throws InvalidConfigurationException {
        this(file.getName(), file);
    }

    public SimpleYMLConfig(final String name, final File file) throws InvalidConfigurationException {
        this(name, file, YamlConfiguration.loadConfiguration(file));
    }

    public SimpleYMLConfig(final File file, final FileConfiguration config) throws InvalidConfigurationException {
        this(file.getName(), file, config);
    }

    public SimpleYMLConfig(final String name, final File file, final FileConfiguration config) throws InvalidConfigurationException {
        super(name, config);
        this.file = file;
    }

    public boolean save() {
        try {
            ((FileConfiguration) super.config).save(this.file);
            return true;
        } catch (final IOException e) {
            LOG.debug(e.getMessage());
            return false;
        }
    }
}
