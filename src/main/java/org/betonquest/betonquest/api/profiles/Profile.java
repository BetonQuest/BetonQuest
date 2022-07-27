package org.betonquest.betonquest.api.profiles;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface Profile {

    OfflinePlayer getOfflinePlayer();

    Player getPlayer();

    Optional<Player> getOptionalPlayer();

    boolean isOnline();

    @Deprecated
    String getPlayerId();


    String getPlayerName();
}
