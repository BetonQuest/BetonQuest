package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
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
     * The font registry used for the conversation.
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
        final ComponentLineWrapper componentLineWrapper = new ComponentLineWrapper(fontRegistry, 320);
        return new SlowTellrawConvIO(conversation, onlineProfile, componentLineWrapper, colors);
    }
}
