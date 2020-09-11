package pl.betoncraft.betonquest.notify;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.Map;

/**
 * Use Actionbar for Notification
 * <p>
 * Data Values:
 */
public class ActionBarNotifyIO extends NotifyIO {


    // Variables

    public ActionBarNotifyIO(final Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(final String message, final Collection<? extends Player> players) {
        final BaseComponent[] textMessage = TextComponent.fromLegacyText(Utils.format(message));

        for (final Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textMessage);
        }

        super.sendNotify(message, players);
    }
}
