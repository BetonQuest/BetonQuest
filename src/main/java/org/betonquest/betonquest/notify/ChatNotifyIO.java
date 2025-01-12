package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final Conversation conversation = Conversation.getConversation(onlineProfile);
        if (conversation == null || conversation.getInterceptor() == null) {
            onlineProfile.getPlayer().sendMessage(message);
        } else {
            conversation.getInterceptor().sendMessage(message);
        }
    }
}
