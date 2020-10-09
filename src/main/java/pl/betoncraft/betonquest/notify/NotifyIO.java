package pl.betoncraft.betonquest.notify;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Used to display messages to a player
 * <p>
 * Data Values:
 * * sound: {sound_name} - What sound to play
 */
public abstract class NotifyIO {

    private final Map<String, String> data;

    protected NotifyIO(final Map<String, String> data) {
        this.data = data;
    }

    protected NotifyIO() {
        this.data = new HashMap<>();
    }

    public Map<String, String> getData() {
        return data;
    }

    /**
     * Set a NotifyIO data option
     *
     * @param key   Data Key
     * @param value Data Value
     * @return ourself to allow chaining
     */
    public NotifyIO set(final String key, final String value) {
        data.put(key, value);
        return this;
    }

    public void sendToAll(final String packName, final String message) {
        final Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        sendNotify(packName, message, players.toArray(new Player[players.size()]));
    }

    /**
     * Show to Specific Players
     *
     * @param players Players to show
     */

    public void sendNotify(final String packName, final String message, final Player... players) {
        final HashMap<Player, String> playerMessages = new HashMap<>();
        if (packName == null) {
            for (final Player player : players) {
                playerMessages.put(player, message);
            }
        } else {
            for (final String variable : BetonQuest.resolveVariables(message)) {
                for (final Player player : players) {
                    final String resolvedMessage = message.replace(variable, BetonQuest.getInstance().getVariableValue(packName, variable, PlayerConverter.getID(player)));
                    playerMessages.put(player, resolvedMessage);
                }
            }
        }
        sendNotify(playerMessages);
    }

    /**
     * Show a notify to a collection of players
     */
    public abstract void sendNotify(final HashMap<Player, String> playerMessages);

    protected void sendNotificationSound(final Set<Player> players) {
        if (getData().containsKey("sound")) {
            for (final Player player : players) {
                try {
                    player.playSound(player.getLocation(), Sound.valueOf(getData().get("sound")), 1F, 1F);
                } catch (IllegalArgumentException e) {
                    player.playSound(player.getLocation(), getData().get("sound"), 1F, 1F);
                    LogUtils.getLogger().log(Level.WARNING, "Could not play the right sound: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
        }
    }
}
