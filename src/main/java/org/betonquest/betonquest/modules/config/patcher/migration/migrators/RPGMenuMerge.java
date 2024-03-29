package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles the RPGMenuMerge migration.
 */
public class RPGMenuMerge implements Migration {

    /**
     * Creates a new RPGMenuMerge migrator.
     */
    public RPGMenuMerge() {
    }

    @Override
    public void migrate() throws IOException {
        final Path rpgmenu = Paths.get("plugins/BetonQuest/rpgmenu.config.yml");
        if (Files.exists(rpgmenu)) {
            Files.move(rpgmenu, rpgmenu.resolveSibling("menuConfig.yml"));
        }
    }
}
