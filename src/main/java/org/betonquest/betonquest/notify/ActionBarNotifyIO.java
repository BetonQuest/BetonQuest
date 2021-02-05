package org.betonquest.betonquest.notify;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ActionBarNotifyIO extends NotifyIO {

    public ActionBarNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        final BaseComponent[] textMessage = TextComponent.fromLegacyText(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textMessage);
    }
}
