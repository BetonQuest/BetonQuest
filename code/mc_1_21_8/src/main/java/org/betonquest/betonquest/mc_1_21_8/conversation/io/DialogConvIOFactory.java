package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Factory for creating {@link DialogConvIO} instances.
 */
public class DialogConvIOFactory implements ConversationIOFactory {

    /** The plugin configuration accessor. */
    private final ConfigAccessor config;

    /** The conversation colors. */
    private final ConversationColors colors;

    /** The component line wrapper used to calculate text widths. */
    private final ComponentLineWrapper componentLineWrapper;

    private final TextParser textParser;

    /**
     * Creates a new DialogConvIOFactory instance.
     *
     * @param config               the plugin configuration accessor
     * @param colors               the conversation colors
     * @param componentLineWrapper the component line wrapper
     * @param textParser           the text parser used to parse text
     */
    public DialogConvIOFactory(
            final ConfigAccessor config,
            final ConversationColors colors,
            final ComponentLineWrapper componentLineWrapper,
            final TextParser textParser
    ) {
        this.config = config;
        this.colors = colors;
        this.componentLineWrapper = componentLineWrapper;
        this.textParser = textParser;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new DialogConvIO(conversation, onlineProfile, config, colors, componentLineWrapper, textParser);
    }
}
