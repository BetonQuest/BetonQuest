package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Displays the message in the chat.
 */
public class ChatNotifyIO extends NotifyIO {

    /**
     * Create a new Chat Notify IO.
     *
     * @param pack the source pack to resolve variables
     * @param data the customization data for notifications
     * @throws QuestException when data could not be parsed
     */
    public ChatNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final Conversation conversation = Conversation.getConversation(onlineProfile);
        if (conversation == null) {
            onlineProfile.getPlayer().sendMessage(message);
        } else {
            conversation.sendMessage(message);
        }
    }
}
