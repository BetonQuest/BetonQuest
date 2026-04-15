package org.betonquest.betonquest.conversation.io;

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
 * Simple chat-based conversation output.
 */
public class SimpleConvIOFactory implements ConversationIOFactory {

    /**
     * The logger factory to create new logger instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Simple conversation IO factory.
     *
     * @param loggerFactory the logger factory to create new logger instances
     * @param config        the plugin configuration accessor
     * @param plugin        the plugin instance
     * @param message       the plugin message instance
     * @param colors        the colors used for the conversation
     */
    public SimpleConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final ConfigAccessor config, final Plugin plugin,
                               final PluginMessage message, final ConversationColors colors) {
        this.loggerFactory = loggerFactory;
        this.config = config;
        this.plugin = plugin;
        this.message = message;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        return new SimpleConvIO(loggerFactory.create(SimpleConvIO.class), config, plugin, message, conversation, onlineProfile, colors);
    }
}
