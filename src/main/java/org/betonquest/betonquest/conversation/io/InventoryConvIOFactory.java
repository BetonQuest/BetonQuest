package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.InventoryConvIO;

/**
 * Factory to create {@link InventoryConvIO}s.
 */
public class InventoryConvIOFactory implements ConversationIOFactory {
    /**
     * Logger Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Configuration to read io options.
     */
    private final ConfigAccessor config;

    /**
     * If the IO should also print the messages in the chat.
     */
    private final boolean printMessages;

    /**
     * Create a new inventory conversation IO factory.
     *
     * @param loggerFactory the logger factory to create new conversation specific loggers
     * @param config        the config to read io options from
     * @param printMessages if the IO should also print the messages in the chat
     */
    public InventoryConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final ConfigAccessor config,
                                  final boolean printMessages) {
        this.loggerFactory = loggerFactory;
        this.config = config;
        this.printMessages = printMessages;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final ConversationColors.Colors colors = ConversationColors.getColors();
        final boolean showNumber = config.getBoolean("conversation.io.chest.show_number", true);
        final boolean showNPCText = config.getBoolean("conversation.io.chest.show_npc_text", true);
        final BetonQuestLogger log = loggerFactory.create(InventoryConvIO.class);
        return new InventoryConvIO(conversation, onlineProfile, log, colors, showNumber, showNPCText, printMessages);
    }
}
