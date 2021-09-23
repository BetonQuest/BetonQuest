package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
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
@SuppressWarnings({"PMD.CommentRequired", "PMD.AtLeastOneConstructor"})
public class TagCalculator implements ContextCalculator<Player> {

    public static final String KEY = "betonquest:tag";

    private final BetonQuest betonQuest = BetonQuest.getInstance();

    /**
     * Calculates all tag contexts that a player has active.
     *
     * @param player          to check contexts for
     * @param contextConsumer accepts contexts
     */
    @Override
    public void calculate(final Player player, final ContextConsumer contextConsumer) {
        Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            final String uuid = PlayerConverter.getID(player);

            final PlayerData data = betonQuest.getPlayerData(uuid);
            if (data != null) {
                data.getTags().forEach(tag -> contextConsumer.accept(KEY, tag));
            }

            final List<String> globalData = betonQuest.getGlobalData().getTags();
            globalData.forEach(tag -> contextConsumer.accept(KEY, tag));
        });
    }
}


