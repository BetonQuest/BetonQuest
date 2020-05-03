package pl.betoncraft.betonquest.id;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

public class PlayerID {
    private String playerID;

    public PlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public UUID toUUID() {
        return UUID.fromString(playerID);
    }

    public Player toPlayer() {
        return PlayerConverter.getPlayer(playerID);
    }
}
