package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);
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
