package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.Map;

/**
 * Use Chat for Notification
 */
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(final String message, final Collection<? extends Player> players) {
        for (final Player player : players) {
            player.sendMessage(Utils.format(message));
        }

        sendNotificationSound(players);
    }
}
