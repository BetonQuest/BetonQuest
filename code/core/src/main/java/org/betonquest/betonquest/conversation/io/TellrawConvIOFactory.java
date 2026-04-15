package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.plugin.Plugin;

/**
 * Tellraw conversation output.
 */
public class TellrawConvIOFactory implements ConversationIOFactory {

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
     * The Localizations instance.
     */
    private final Localizations localizations;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Tellraw conversation IO factory.
     *
     * @param loggerFactory the logger factory to create new logger instances
     * @param config        the plugin configuration accessor
     * @param plugin        the plugin instance
     * @param localizations the Localizations instance
     * @param colors        the colors used for the conversation
     */
    public TellrawConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final ConfigAccessor config, final Plugin plugin,
                                final Localizations localizations, final ConversationColors colors) {
        this.loggerFactory = loggerFactory;
        this.config = config;
        this.plugin = plugin;
        this.localizations = localizations;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        return new TellrawConvIO(loggerFactory.create(TellrawConvIO.class), config, plugin, localizations, conversation, onlineProfile, colors);
    }
}
