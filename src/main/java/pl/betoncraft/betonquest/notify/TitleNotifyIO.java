package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Use Title Popup for Notification
 * <p>
 * Data Valuues:
 * * fadeIn: seconds to fade in
 * * stay: seconds to stay
 * * fadeOut: seconds to fade out
 * * subTitle: the subtitle to show, else blank
 */
public class TitleNotifyIO extends NotifyIO {


    // Variables

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final String subTitle;


    public TitleNotifyIO(final Map<String, String> data) {
        super(data);

        fadeIn = Integer.parseInt(data.getOrDefault("fadein", "10"));
        stay = Integer.parseInt(data.getOrDefault("stay", "70"));
        fadeOut = Integer.parseInt(data.getOrDefault("fadeout", "20"));
        subTitle = getData().getOrDefault("subtitle", "").replace("_", " ");
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
        for (final Map.Entry<Player, String> entry : playerMessages.entrySet()) {
            entry.getKey().sendTitle(Utils.format(entry.getValue()), Utils.format(subTitle), fadeIn, stay, fadeOut);
        }
        sendNotificationSound(playerMessages.keySet());
    }
}
