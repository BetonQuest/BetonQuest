package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Static helpers for working with {@link Selector}s.
 */
public final class Selectors {
    private Selectors() {
    }

    /**
     * Creates a selector that will select the target by calling the given function with the offline player.
     *
     * @param <T>                   type of the target to be selected
     * @param offlinePlayerToTarget function for obtaining the target from an offline player
     * @return the created selector
     */
    public static <T> Selector<T> fromOfflinePlayer(final Function<? super OfflinePlayer, ? extends T> offlinePlayerToTarget) {
        return profile -> offlinePlayerToTarget.apply(profile == null ? null : profile.getPlayer());
    }

    /**
     * Creates a selector that will select the target by calling the given function with the online profile
     * or fail with a {@link QuestException} if the player is not online.
     *
     * @param <T>                   type of the target to be selected
     * @param onlineProfileToTarget function for obtaining the target from an online profile
     * @return the created selector
     */
    public static <T> Selector<T> fromOnlineProfile(final Function<? super OnlineProfile, ? extends T> onlineProfileToTarget) {
        return profile -> {
            if (profile == null) {
                throw new QuestException("Profile must be present.");
            }
            return profile.getOnlineProfile()
                    .map(onlineProfileToTarget)
                    .orElseThrow(() -> new QuestException("Player must be online."));
        };
    }

    /**
     * Creates a selector that will select the target by calling the given function with the player
     * or fail with a {@link QuestException} if the player is not online.
     *
     * @param <T>            type of the target to be selected
     * @param playerToTarget function for obtaining the target from a player
     * @return the created selector
     */
    public static <T> Selector<T> fromPlayer(final Function<? super Player, ? extends T> playerToTarget) {
        return fromOnlineProfile(playerToTarget.compose(OnlineProfile::getPlayer));
    }
}
