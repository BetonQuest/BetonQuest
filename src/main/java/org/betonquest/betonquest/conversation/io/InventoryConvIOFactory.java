package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
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
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Configuration to read io options.
     */
    private final ConfigAccessor config;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors to use for the conversation.
     */
    private final ConversationColors colors;

    /**
     * If the IO should also print the messages in the chat.
     */
    private final boolean printMessages;

    /**
     * Create a new inventory conversation IO factory.
     *
     * @param packManager   the quest package manager to get quest packages from
     * @param loggerFactory the logger factory to create new conversation specific loggers
     * @param config        the config to read io options from
     * @param fontRegistry  the font registry to use for the inventory
     * @param colors        the colors to use for the conversation
     * @param printMessages if the IO should also print the messages in the chat
     */
    public InventoryConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                  final ConfigAccessor config, final FontRegistry fontRegistry,
                                  final ConversationColors colors, final boolean printMessages) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.config = config;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
        this.printMessages = printMessages;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final boolean showNumber = config.getBoolean("conversation.io.chest.show_number", true);
        final boolean showNPCText = config.getBoolean("conversation.io.chest.show_npc_text", true);
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 270);
        final BetonQuestLogger log = loggerFactory.create(InventoryConvIO.class);
        return new InventoryConvIO(conversation, onlineProfile, log, packManager, colors, showNumber, showNPCText, printMessages, componentLineWrapper);
    }
}
