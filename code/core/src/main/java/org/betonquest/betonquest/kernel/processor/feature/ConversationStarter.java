package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.bukkit.event.PlayerConversationStartEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationID;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Starts conversations from their id.
 */
public class ConversationStarter {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The map of all active conversations.
     */
    private final Map<Profile, Conversation> activeConversations;

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Factory for the normal Conversation.
     */
    private final ConversationFactory standardFactory;

    /**
     * Creates a new Starter for Conversations.
     *
     * @param loggerFactory       the logger factory to create new class specific loggers
     * @param log                 the logger for this class
     * @param activeConversations the list of conversations to add started
     * @param plugin              the plugin to start tasks
     * @param pluginMessage       the {@link PluginMessage} instance
     */
    public ConversationStarter(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                               final Map<Profile, Conversation> activeConversations, final Plugin plugin,
                               final PluginMessage pluginMessage) {
        this.log = log;
        this.activeConversations = activeConversations;
        this.plugin = plugin;
        this.standardFactory = (onlineProfile, conversationID, center, endCallable)
                -> new Conversation(loggerFactory.create(Conversation.class), pluginMessage, onlineProfile, conversationID,
                center, endCallable);
    }

    /**
     * Creates and starts a conversation.
     *
     * @param onlineProfile  the profile to start the conversation for
     * @param conversationID the id of the conversation to start
     * @param center         the location where the conversation should start
     * @param startingOption the name of the option where the conversation should forcibly start at
     */
    public void startConversation(final OnlineProfile onlineProfile, final ConversationID conversationID,
                                  final Location center, @Nullable final String startingOption) {
        startConversation(onlineProfile, conversationID, center, startingOption, standardFactory);
    }

    /**
     * Creates and starts a conversation.
     *
     * @param onlineProfile  the profile to start the conversation for
     * @param conversationID the id of the conversation to start
     * @param center         the location where the conversation should start
     * @param startingOption the name of the option where the conversation should forcibly start at
     * @param factory        the factory that creates the conversation to start
     */
    public void startConversation(final OnlineProfile onlineProfile, final ConversationID conversationID,
                                  final Location center, @Nullable final String startingOption, final ConversationFactory factory) {
        if (activeConversations.containsKey(onlineProfile)) {
            log.debug(conversationID.getPackage(), onlineProfile + " is in conversation right now, returning.");
            return;
        }

        try {
            final Conversation conversation = factory.create(
                    onlineProfile, conversationID, center, () -> activeConversations.remove(onlineProfile));
            activeConversations.put(onlineProfile, conversation);
            final QuestPackage pack = conversationID.getPackage();
            log.debug(pack, "Starting conversation '" + conversationID + "' for '" + onlineProfile + "'.");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (!new PlayerConversationStartEvent(onlineProfile, conversation).callEvent()) {
                        log.debug(pack, "Conversation '" + conversationID + "' for '" + onlineProfile + "' has been "
                                + "canceled because its PlayerConversationStartEvent has been canceled.");
                        activeConversations.remove(onlineProfile);
                    }
                    if (startingOption == null) {
                        new Conversation.Starter(conversation).start();
                    } else {
                        new Conversation.Starter(conversation, startingOption).start();
                    }
                } catch (final QuestException e) {
                    log.error("Cannot continue starting conversation without options: " + e, e);
                    activeConversations.remove(onlineProfile);
                }
            });
        } catch (final QuestException e) {
            log.error(conversationID.getPackage(), "Cannot create conversation '" + conversationID + "': " + e.getMessage(), e);
        }
    }

    /**
     * Allows to create custom Conversations.
     */
    @FunctionalInterface
    public interface ConversationFactory {

        /**
         * Creates a new conversation between player and npc at given location.
         *
         * @param onlineProfile  the {@link OnlineProfile} of the player
         * @param conversationID ID of the conversation
         * @param center         location where the conversation has been started
         * @param endCallable    the callable that removes the conversation from the active ones
         * @return the newly created conversation
         * @throws QuestException when required conversation objects could not be created
         */
        Conversation create(OnlineProfile onlineProfile, ConversationID conversationID, Location center,
                            Runnable endCallable) throws QuestException;
    }
}
