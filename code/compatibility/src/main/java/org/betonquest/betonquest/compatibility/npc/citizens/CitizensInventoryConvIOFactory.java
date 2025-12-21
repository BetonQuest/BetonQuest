package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Factory to create {@link CitizensInventoryConvIO}s.
 */
public class CitizensInventoryConvIOFactory implements ConversationIOFactory {

    /**
     * Logger Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors to use for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Configuration to read io options.
     */
    private final ConfigAccessor config;

    /**
     * If the IO should also print the messages in the chat.
     */
    private final boolean printMessages;

    /**
     * Create a new inventory conversation IO factory for citizens specific heads.
     *
     * @param loggerFactory the logger factory to create new conversation specific loggers
     * @param placeholders  the {@link Placeholders} to create and resolve placeholders
     * @param packManager   the quest package manager to get quest packages from
     * @param fontRegistry  the font registry to use for the conversation
     * @param colors        the colors to use for the conversation
     * @param config        the config to use for the conversation
     * @param printMessages if the IO should also print the messages in the chat
     */
    public CitizensInventoryConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final Placeholders placeholders,
                                          final QuestPackageManager packManager, final FontRegistry fontRegistry,
                                          final ConversationColors colors, final ConfigAccessor config,
                                          final boolean printMessages) {
        this.loggerFactory = loggerFactory;
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
        this.config = config;
        this.printMessages = printMessages;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        final boolean showNumber = config.getBoolean("conversation.io.chest.show_number", true);
        final boolean showNPCText = config.getBoolean("conversation.io.chest.show_npc_text", true);
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 270);
        final BetonQuestLogger log = loggerFactory.create(CitizensInventoryConvIO.class);
        return new CitizensInventoryConvIO(conversation, onlineProfile, log, placeholders, packManager, colors, showNumber, showNPCText,
                printMessages, componentLineWrapper);
    }
}
