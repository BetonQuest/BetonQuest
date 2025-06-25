package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * SlowTellraw conversation output.
 */
public class SlowTellrawConvIOFactory implements ConversationIOFactory {
    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new SlowTellraw conversation IO factory.
     *
     * @param fontRegistry The font registry used for the conversation.
     * @param colors       The colors used for the conversation.
     */
    public SlowTellrawConvIOFactory(final FontRegistry fontRegistry, final ConversationColors colors) {
        this.fontRegistry = fontRegistry;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 320);
        int messageDelay = BetonQuest.getInstance().getPluginConfig().getInt("conversation.io.slowtellraw.message_delay", 10);
        if (messageDelay <= 0) {
            BetonQuest.getInstance().getLogger().warning("Invalid message delay of " + messageDelay + " for SlowTellraw Conversation IO, using default value of 10 ticks");
            messageDelay = 10;
        }
        return new SlowTellrawConvIO(conversation, onlineProfile, messageDelay, componentLineWrapper, colors);
    }
}
