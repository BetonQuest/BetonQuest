package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Use Chat for Notification
 */
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
        for (final Map.Entry<Player, String> entry : playerMessages.entrySet()) {
            entry.getKey().sendMessage(Utils.format(entry.getValue()));
        }
        sendNotificationSound(playerMessages.keySet());
    }
}
