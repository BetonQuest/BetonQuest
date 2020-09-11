package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.Map;

/**
 * Use Title Popup for Notification using SubTitle only
 * <p>
 * Data Valuues:
 * * fadeIn: ticks to fade in
 * * stay: ticks to stay
 * * fadeOut: ticks to fade out
 */
public class SubTitleNotifyIO extends NotifyIO {


    // Variables

    private int fadeIn;
    private int stay;
    private int fadeOut;


    public SubTitleNotifyIO(final Map<String, String> data) {
        super(data);

        fadeIn = Integer.valueOf(data.getOrDefault("fadein", "10"));
        stay = Integer.valueOf(data.getOrDefault("stay", "70"));
        fadeOut = Integer.valueOf(data.getOrDefault("fadeout", "20"));
    }

    @Override
    public void sendNotify(final String message, final Collection<? extends Player> players) {
        for (final Player player : players) {
            player.sendTitle("", Utils.format(message), fadeIn, stay, fadeOut);
        }

        super.sendNotify(message, players);
    }
}
