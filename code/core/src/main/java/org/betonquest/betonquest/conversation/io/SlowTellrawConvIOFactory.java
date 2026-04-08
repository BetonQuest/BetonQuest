package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.plugin.Plugin;

/**
 * SlowTellraw conversation output.
 */
public class SlowTellrawConvIOFactory implements ConversationIOFactory {

    /**
     * The logger factory to create new logger instances.
     */
    private final BetonQuestLoggerFactory factory;

    /**
     * The plugin configuration accessor.
     */
    private final ConfigAccessor config;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The plugin message instance.
     */
    private final PluginMessage message;

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
     * @param factory      the logger factory to create new logger instances
     * @param config       the plugin configuration accessor
     * @param plugin       the plugin instance
     * @param message      the plugin message instance
     * @param fontRegistry The font registry used for the conversation.
     * @param colors       The colors used for the conversation.
     */
    public SlowTellrawConvIOFactory(final BetonQuestLoggerFactory factory, final ConfigAccessor config, final Plugin plugin,
                                    final PluginMessage message, final FontRegistry fontRegistry, final ConversationColors colors) {
        this.factory = factory;
        this.config = config;
        this.plugin = plugin;
        this.message = message;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 320);
        final int messageDelay = config.getInt("conversation.io.slowtellraw.message_delay", 10);
        if (messageDelay <= 0) {
            throw new QuestException("Invalid message delay of %d for SlowTellraw Conversation IO!".formatted(messageDelay));
        }
        return new SlowTellrawConvIO(factory.create(SlowTellrawConvIO.class), config, plugin, message, conversation, onlineProfile, messageDelay, componentLineWrapper, colors);
    }
}
