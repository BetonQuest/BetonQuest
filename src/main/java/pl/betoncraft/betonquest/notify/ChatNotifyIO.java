package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Use Chat for Notification
 */
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(message);
        }

        super.sendNotify(message, players);
    }
}
