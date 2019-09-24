package pl.betoncraft.betonquest.config.pack;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

public class ConfigPack extends ConfigContainer {
    private final HashMap<String, QuestCanceler> cancelers;

    public ConfigPack(File packFile, String packName) {
        super(packFile, packName);
        cancelers = new HashMap<>();

        loadCancelers();
    }

    private void loadCancelers() {
        ConfigurationSection s = getMain().getConfig().getConfigurationSection("cancel");
        if (s == null)
            return;
        for (String key : s.getKeys(false)) {
            String name = getName() + "." + key;
            try {
                cancelers.put(name, new QuestCanceler(name));
            } catch (InstructionParseException e) {
                LogUtils.getLogger().log(Level.WARNING,"Could not load '" + name + "' quest canceler: " + e.getMessage());
            }
        }
    }
}
