package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public final class MMOCoreUtils {
    @SuppressWarnings("NullAway.Init")
    private static Configuration mmoCoreAttributeConfig;

    private MMOCoreUtils() {
    }

    public static void loadMMOCoreAttributeConfig() {
        mmoCoreAttributeConfig = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("MMOCore").getDataFolder(), "attributes.yml"));
    }

    public static int getMMOCoreAttribute(final UUID uuid, final String attribute) {
        final PlayerAttributes attributes = PlayerData.get(uuid).getAttributes();
        return attributes.getAttribute(new PlayerAttribute(mmoCoreAttributeConfig.getConfigurationSection(attribute)));
    }

    public static void isMMOConfigValidForAttribute(final String attributeName) throws QuestException {
        if (!mmoCoreAttributeConfig.contains(attributeName)) {
            throw new QuestException("Couldn't find the attribute \"" + attributeName + "\" in the MMOCore attribute config!");
        }
    }
}
