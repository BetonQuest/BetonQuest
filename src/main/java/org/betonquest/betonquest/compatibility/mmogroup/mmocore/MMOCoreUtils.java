package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Allows to get an attribute from a loaded configuration.
 */
public final class MMOCoreUtils {
    /**
     * Configuration to get attributes.
     */
    private final FileConfigAccessor mmoCoreAttributeConfig;

    /**
     * Creates a new Utils class.
     *
     * @param configAccessorFactory the factory to create the config accessor
     * @param mmoCoreDataFolder     the folder where the attributes are stored
     * @throws FileNotFoundException         if the file is not found
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    public MMOCoreUtils(final ConfigAccessorFactory configAccessorFactory, final File mmoCoreDataFolder) throws FileNotFoundException, InvalidConfigurationException {
        mmoCoreAttributeConfig = configAccessorFactory.create(new File(mmoCoreDataFolder, "attributes.yml"));
    }

    /**
     * Reloads the backing config.
     *
     * @throws IOException if the config could not be reloaded
     */
    public void reload() throws IOException {
        mmoCoreAttributeConfig.reload();
    }

    /**
     * Checks if an attribute is present and gets it.
     *
     * @param attributeName the name of the attribute to get
     * @return the attribute with that name
     * @throws QuestException when there is no such attribute
     */
    public PlayerAttribute getAttribute(final String attributeName) throws QuestException {
        if (mmoCoreAttributeConfig.contains(attributeName)) {
            return new PlayerAttribute(mmoCoreAttributeConfig.getConfigurationSection(attributeName));
        }
        throw new QuestException("Couldn't find the attribute \"" + attributeName + "\" in the MMOCore attribute config!");
    }
}
