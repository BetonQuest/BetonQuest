package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.io.File;
import java.util.UUID;


public final class MMOCoreUtils {

    public static YamlConfiguration mmoCoreAttributeConfig;

    private MMOCoreUtils() {
    }

    public static void loadMMOCoreAttributeConfig() {
        mmoCoreAttributeConfig = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("MMOCore").getDataFolder(), "attributes.yml"));
    }

    public static int getMMOCoreAttribute(final UUID uuid, final String attribute) {
        final PlayerAttributes attributes = PlayerData.get(uuid).getAttributes();
        return attributes.getAttribute(new PlayerAttribute(mmoCoreAttributeConfig.getConfigurationSection(attribute)));
    }

    public static void isMMOConfigValidForAttribute(final String attributeName) throws InstructionParseException {
        if (!mmoCoreAttributeConfig.contains(attributeName)) {
            throw new InstructionParseException("Couldn't find the attribute \"" + attributeName + "\" in the MMOCore attribute config!");
        }
    }
}
