package org.betonquest.betonquest.conversation.io;

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
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Factory to create {@link InventoryConvIO}s.
 */
public class InventoryConvIOFactory implements ConversationIOFactory {

    /**
     * Logger Factory to create new class-specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * The plugin instance to use.
     */
    private final Plugin plugin;

    /**
     * The plugin manager instance to use.
     */
    private final PluginManager pluginManager;

    /**
     * The plugin message instance to use.
     */
    private final PluginMessage pluginMessage;

    /**
     * The instructions instance to use.
     */
    private final Instructions instructions;

    /**
     * The conversations instance to use.
     */
    private final Conversations conversations;

    /**
     * The item manager instance to use.
     */
    private final ItemManager itemManager;

    /**
     * The profile provider instance to use.
     */
    private final ProfileProvider profileProvider;

    /**
     * If the IO should also print the messages in the chat.
     */
    private final boolean printMessages;

    /**
     * Create a new inventory conversation IO factory.
     *
     * @param parameters    the parameters to use for the factory
     * @param printMessages if the IO should also print the messages in the chat
     */
    public InventoryConvIOFactory(final ConstructorParameters parameters, final boolean printMessages) {
        this.loggerFactory = parameters.loggerFactory();
        this.config = parameters.config();
        this.fontRegistry = parameters.fontRegistry();
        this.colors = parameters.colors();
        this.plugin = parameters.plugin();
        this.pluginManager = parameters.pluginManager();
        this.pluginMessage = parameters.pluginMessage();
        this.instructions = parameters.instructions();
        this.conversations = parameters.conversations();
        this.itemManager = parameters.itemManager();
        this.profileProvider = parameters.profileProvider();
        this.printMessages = printMessages;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        final boolean showNumber = config.getBoolean("conversation.io.chest.show_number", true);
        final boolean showNPCText = config.getBoolean("conversation.io.chest.show_npc_text", true);
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, 270);
        final BetonQuestLogger log = loggerFactory.create(InventoryConvIO.class);
        return new InventoryConvIO(conversation, onlineProfile, log, plugin, pluginManager, instructions, pluginMessage,
                itemManager, profileProvider, conversations, colors, showNumber, showNPCText, printMessages, componentLineWrapper);
    }

    /**
     * The constructor parameters for the factory.
     *
     * @param loggerFactory   the logger factory to create logger instances
     * @param config          the betonquest config accessor
     * @param fontRegistry    the font registry to access font details
     * @param colors          the colors to use for the conversation
     * @param plugin          the plugin instance
     * @param pluginManager   the plugin manager instance
     * @param pluginMessage   the plugin message instance
     * @param instructions    the instructions instance
     * @param conversations   the conversations instance
     * @param itemManager     the item manager instance
     * @param profileProvider the profile provider instance
     */
    public record ConstructorParameters(BetonQuestLoggerFactory loggerFactory, ConfigAccessor config,
                                        FontRegistry fontRegistry, ConversationColors colors, Plugin plugin,
                                        PluginManager pluginManager, PluginMessage pluginMessage,
                                        Instructions instructions, Conversations conversations, ItemManager itemManager,
                                        ProfileProvider profileProvider) {

    }
}
