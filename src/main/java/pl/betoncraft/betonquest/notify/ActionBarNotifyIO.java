package pl.betoncraft.betonquest.notify;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Use Actionbar for Notification
 *
 * Data Values:
 *
 */
public class ActionBarNotifyIO extends NotifyIO {


    // Variables

    public ActionBarNotifyIO(Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        BaseComponent[] textMessage = TextComponent.fromLegacyText(message);

        for (Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textMessage);
        }

        super.sendNotify(message, players);
    }
}
