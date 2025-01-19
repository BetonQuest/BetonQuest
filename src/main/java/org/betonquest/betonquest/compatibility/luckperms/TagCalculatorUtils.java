package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Provides all per-player tags and all global tags as LuckPerms
 * contexts.
 */
public final class TagCalculatorUtils {
    /**
     * The BetonQuest tag.
     */
    public static final String KEY_LOCAL = "betonquest:tag:";

    /**
     * The global BetonQuest tag.
     */
    public static final String KEY_GLOBAL = "betonquest:globaltag:";

    private TagCalculatorUtils() {
    }

    /**
     * Get an anonymous ContextCalculator. It has to be anonymous to prevent the loading of the class when no LP is installed.
     *
     * @return a ContextCalculator
     */
    public static ContextCalculator<Player> getTagContextCalculator() {
        return (player, contextConsumer) -> {
            if (player.isOnline()) {
                final PlayerData data = BetonQuest.getInstance().getPlayerDataStorage().get(PlayerConverter.getID(player));
                data.getTags().forEach(tag -> contextConsumer.accept(KEY_LOCAL + tag, "true"));
            }

            final List<String> globalData = BetonQuest.getInstance().getGlobalData().getTags();
            globalData.forEach(tag -> contextConsumer.accept(KEY_GLOBAL + tag, "true"));
        };
    }
}
