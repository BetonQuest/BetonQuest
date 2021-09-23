package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Provides all per-player tags and all global tags as LuckPerms
 * contexts.
 */
public final class TagCalculatorUtils {
    /**
     * The BetonQuest tag
     */
    public static final String KEY = "betonquest:tag";

    private TagCalculatorUtils() {
    }

    /**
     * Get an anonymous ContextCalculator. It has to be anonymous to prevent the loading of the class when no LP is installed.
     *
     * @return a {@link ContextCalculator<Player>}
     */
    public static ContextCalculator<Player> getTagContextCalculator() {
        return (player, contextConsumer) -> Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            final String uuid = PlayerConverter.getID(player);

            final PlayerData data = BetonQuest.getInstance().getPlayerData(uuid);
            if (data != null) {
                data.getTags().forEach(tag -> contextConsumer.accept(KEY, tag));
            }

            final List<String> globalData = BetonQuest.getInstance().getGlobalData().getTags();
            globalData.forEach(tag -> contextConsumer.accept(KEY, tag));
        });
    }
}
