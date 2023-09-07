package org.betonquest.betonquest.modules.config.patcher.migration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for determine in which version of BetonQuest the config is.
 */
public class Determiner {

    /**
     * @return version of BetonQuest in which the config is
     */
    public int getVersion() {
        final File rpgmenu = new File("plugins/BetonQuest/rpgmenu.config.yml");
        final Path questPackages = Paths.get("plugins/BetonQuest/QuestPackages");
        if (rpgmenu.exists()) {
            return 0;
        }
        if (!Files.exists(questPackages)) {
            return 98;
        }
        return 238;
        //TODO: Implement
    }
}
