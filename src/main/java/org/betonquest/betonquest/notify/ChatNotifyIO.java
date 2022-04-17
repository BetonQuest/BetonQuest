package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final QuestPackage pack, final Map<String, String> data) throws InstructionParseException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        final Conversation conversation = Conversation.getConversation(PlayerConverter.getID(player));
        if (conversation == null || conversation.getInterceptor() == null) {
            player.sendMessage(message);
        } else {
            conversation.getInterceptor().sendMessage(message);
        }
    }
}
