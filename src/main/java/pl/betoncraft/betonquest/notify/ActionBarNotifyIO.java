package pl.betoncraft.betonquest.notify;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Use Actionbar for Notification
 * <p>
 * Data Values:
 */
public class ActionBarNotifyIO extends NotifyIO {

    public ActionBarNotifyIO(final Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
        for (final Map.Entry<Player,String> entry : playerMessages.entrySet()) {
            final BaseComponent[] textMessage = TextComponent.fromLegacyText(Utils.format(entry.getValue()));
            entry.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, textMessage);
        }
        sendNotificationSound(playerMessages.keySet());
    }
}
