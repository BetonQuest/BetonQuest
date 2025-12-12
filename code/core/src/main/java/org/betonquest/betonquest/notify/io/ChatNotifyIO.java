package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Displays the message in the chat.
 */
public class ChatNotifyIO extends NotifyIO {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a new Chat Notify IO.
     *
     * @param variables       the variable processor to create and resolve variables
     * @param pack            the source pack to resolve variables
     * @param data            the customization data for notifications
     * @param conversationApi the Conversation API
     * @throws QuestException when data could not be parsed
     */
    public ChatNotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data, final ConversationApi conversationApi) throws QuestException {
        super(variables, pack, data);
        this.conversationApi = conversationApi;
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final Conversation conversation = conversationApi.getActive(onlineProfile);
        if (conversation == null) {
            onlineProfile.getPlayer().sendMessage(message);
        } else {
            conversation.sendMessage(message);
        }
    }
}
