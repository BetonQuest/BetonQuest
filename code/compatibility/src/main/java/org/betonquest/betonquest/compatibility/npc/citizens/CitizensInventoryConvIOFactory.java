package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Factory to create {@link CitizensInventoryConvIO}s.
 */
public class CitizensInventoryConvIOFactory implements ConversationIOFactory {

    /**
     * The plugin instance to use.
     */
    private final Plugin plugin;

    /**
     * The plugin manager to use.
     */
    private final PluginManager pluginManager;

    /**
     * The instruction api to use.
     */
    private final Instructions instructions;

    /**
     * The plugin message instance to use.
     */
    private final PluginMessage pluginMessage;

    /**
     * The item manager to use.
     */
    private final ItemManager itemManager;

    /**
     * The profile provider to use.
     */
    private final ProfileProvider profileProvider;

    /**
     * The conversations instance to use.
     */
    private final Conversations conversations;

    /**
     * Logger Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * @param loggerFactory   the logger factory to create new conversation specific loggers
     * @param fontRegistry    the font registry to use for the conversation
     * @param colors          the colors to use for the conversation
     * @param config          the config to use for the conversation
     * @param plugin          the plugin to use for the conversation
     * @param pluginManager   the plugin manager to use for the conversation
     * @param instructions    the instruction api to use for the conversation
     * @param pluginMessage   the plugin message instance to use for the conversation
     * @param itemManager     the item manager to use for the conversation
     * @param profileProvider the profile provider to use for the conversation
     * @param conversations   the conversations instance
     * @param printMessages   if the IO should also print the messages in the chat
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CitizensInventoryConvIOFactory(final BetonQuestLoggerFactory loggerFactory, final FontRegistry fontRegistry,
                                          final ConversationColors colors, final ConfigAccessor config, final Plugin plugin,
                                          final PluginManager pluginManager, final Instructions instructions,
                                          final PluginMessage pluginMessage, final ItemManager itemManager,
                                          final ProfileProvider profileProvider, final Conversations conversations,
                                          final boolean printMessages) {
        this.loggerFactory = loggerFactory;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
        this.config = config;
        this.printMessages = printMessages;
        this.plugin = plugin;
        this.pluginManager = pluginManager;
        this.instructions = instructions;
        this.pluginMessage = pluginMessage;
        this.itemManager = itemManager;
        this.profileProvider = profileProvider;
        this.conversations = conversations;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        final boolean showNumber = config.getBoolean("conversation.io.chest.show_number", true);
        final boolean showNPCText = config.getBoolean("conversation.io.chest.show_npc_text", true);
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 270);
        final BetonQuestLogger log = loggerFactory.create(CitizensInventoryConvIO.class);
        return new CitizensInventoryConvIO(conversation, onlineProfile, log, colors, plugin, pluginManager,
                instructions, pluginMessage, itemManager, profileProvider, conversations, showNumber, showNPCText,
                printMessages, componentLineWrapper);
    }
}
