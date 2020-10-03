package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
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

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;


    public SubTitleNotifyIO(final Map<String, String> data) {
        super(data);

        fadeIn = Integer.parseInt(data.getOrDefault("fadein", "10"));
        stay = Integer.parseInt(data.getOrDefault("stay", "70"));
        fadeOut = Integer.parseInt(data.getOrDefault("fadeout", "20"));
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
        for (final Map.Entry<Player,String> entry : playerMessages.entrySet()) {
            entry.getKey().sendTitle("", Utils.format(entry.getValue()), fadeIn, stay, fadeOut);
        }
        sendNotificationSound(playerMessages.keySet());
    }

}
