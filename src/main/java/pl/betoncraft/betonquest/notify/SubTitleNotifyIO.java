package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Use Title Popup for Notification using SubTitle only
 *
 * Data Valuues:
 *   * fadeIn: ticks to fade in
 *   * stay: ticks to stay
 *   * fadeOut: ticks to fade out
 *
 */
public class SubTitleNotifyIO extends NotifyIO {


    // Variables

    private int fadeIn;
    private int stay;
    private int fadeOut;


    public SubTitleNotifyIO(Map<String, String> data) {
        super(data);

        fadeIn = Integer.valueOf(data.getOrDefault("fadein", "10"));
        stay = Integer.valueOf(data.getOrDefault("stay", "70"));
        fadeOut = Integer.valueOf(data.getOrDefault("fadeout", "20"));
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendTitle("", message, fadeIn, stay, fadeOut);
        }

        super.sendNotify(message, players);
    }
}
