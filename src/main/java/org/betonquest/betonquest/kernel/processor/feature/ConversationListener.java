package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

/**
 * Listener for active conversations.
 * Blocks specific actions and ends/suspends the conversation on logout.
 */
public class ConversationListener implements Listener {

    /**
     * Map of Profiles with their active conversation.
     */
    private final ConversationApi conversationApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Config to load values from.
     */
    private final ConfigAccessor config;

    /**
     * Notification sender when commands are blocked in a conversation.
     */
    private final IngameNotificationSender blockedSender;

    /**
     * List of blocked commands while in conversation.
     */
    private List<String> blacklist = List.of();

    /**
     * Create a new Listener for Conversation interactions.
     *
     * @param log             the custom logger to use for error logging
     * @param conversationApi the Conversation API to get active conversations
     * @param profileProvider the profile provider instance
     * @param pluginMessage   the plugin message instance to use for ingame notifications
     * @param config          the config to load values from
     */
    public ConversationListener(final BetonQuestLogger log, final ConversationApi conversationApi,
                                final ProfileProvider profileProvider, final PluginMessage pluginMessage,
                                final ConfigAccessor config) {
        this.conversationApi = conversationApi;
        this.profileProvider = profileProvider;
        this.config = config;
        this.blockedSender = new IngameNotificationSender(log, pluginMessage, null,
                "Conversation Command Blocked", NotificationLevel.ERROR, "command_blocked");
    }

    /**
     * Reloads the values.
     */
    public void reload() {
        this.blacklist = config.getStringList("conversation.cmd_blacklist");
    }

    /**
     * Blocks blacklisted commands.
     *
     * @param event the preprocess event to eventually cancel
     */
    @EventHandler(ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        final Conversation active = conversationApi.getActive(profile);
        if (active == null) {
            return;
        }
        final String cmdName = event.getMessage().split(" ")[0].substring(1);
        if (blacklist.contains(cmdName)) {
            event.setCancelled(true);
            blockedSender.sendNotification(profile);
        }
    }

    /**
     * Prevent damage to (or from) player while in conversation.
     *
     * @param event the damage event to cancel
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof final Player player) {
            final OnlineProfile profile = profileProvider.getProfile(player);
            final Conversation active = conversationApi.getActive(profile);
            if (active != null && active.getData().getPublicData().invincible()) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof final Player player) {
            final OnlineProfile profile = profileProvider.getProfile(player);
            final Conversation active = conversationApi.getActive(profile);
            if (active != null && active.getData().getPublicData().invincible()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * End conversation if player quits.
     *
     * @param event the quit event
     */
    @EventHandler(ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        final OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        final Conversation active = conversationApi.getActive(profile);
        if (active == null) {
            return;
        }
        if (active.isMovementBlock()) {
            active.suspend();
        } else {
            active.endConversation();
        }
    }
}
