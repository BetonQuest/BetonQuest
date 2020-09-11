package pl.betoncraft.betonquest.notify;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Used to display messages to a player
 * <p>
 * Data Valuues:
 * * sound: {sound_name} - What sound to play
 */
public abstract class NotifyIO {

    private final Map<String, String> data;

    public NotifyIO(final Map<String, String> data) {
        this.data = data;
    }

    public NotifyIO() {
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

    public void sendToAll(final String message) {
        sendNotify(message, Bukkit.getServer().getOnlinePlayers());
    }

    /**
     * Show to Specific Players
     *
     * @param players Players to show
     */

    public void sendNotify(final String message, final Player... players) {
        sendNotify(message, Arrays.asList(players));
    }

    /**
     * Show a notify to a collection of players
     */
    public void sendNotify(final String message, final Collection<? extends Player> players) {
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
