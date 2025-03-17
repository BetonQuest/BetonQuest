package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Allows to get an attribute from a loaded configuration.
 */
public final class MMOCoreUtils {
    /**
     * Configuration to get attributes.
     */
    private Configuration mmoCoreAttributeConfig;

    /**
     * Creates a new Utils class.
     *
     * @param mmoCoreDataFolder the folder where the attributes are stored
     */
    public MMOCoreUtils(final File mmoCoreDataFolder) {
        reload(mmoCoreDataFolder);
    }

    /**
     * Reloads the backing config.
     *
     * @param mmoCoreDataFolder the folder where the attributes are stored
     */
    public void reload(final File mmoCoreDataFolder) {
        mmoCoreAttributeConfig = YamlConfiguration.loadConfiguration(new File(mmoCoreDataFolder, "attributes.yml"));
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
