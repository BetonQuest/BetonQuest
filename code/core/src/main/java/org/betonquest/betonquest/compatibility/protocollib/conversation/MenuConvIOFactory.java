package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * the message parser to parse the configuration messages.
     */
    private final MessageParser messageParser;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * The config accessor to the plugin's configuration.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Menu conversation IO factory.
     *
     * @param messageParser the message parser to parse the configuration messages
     * @param fontRegistry  the font registry used for the conversation
     * @param config        the config accessor to the plugin's configuration
     * @param colors        the colors used for the conversation
     */
    public MenuConvIOFactory(final MessageParser messageParser, final FontRegistry fontRegistry, final ConfigAccessor config, final ConversationColors colors) {
        this.messageParser = messageParser;
        this.fontRegistry = fontRegistry;
        this.config = config;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = MenuConvIOSettings.fromConfigurationSection(messageParser, config.getConfigurationSection("conversation.io.menu"));
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, settings.lineLength());
        return new MenuConvIO(conversation, onlineProfile, colors, settings, componentLineWrapper);
    }
}
