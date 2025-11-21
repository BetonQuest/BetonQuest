package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Resumes the conversation for a disconnected or "fleeing" player.
 */
public class ConversationResumer implements Listener {

    /**
     * The state to resume from.
     */
    private final PlayerConversationState state;

    /**
     * The player to resume the conversation for.
     */
    private final Player player;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * The profile to resume the conversation for.
     */
    private final OnlineProfile onlineProfile;

    /**
     * The maximum distance between the player and the NPC.
     */
    private final double distance;

    /**
     * Creates a new ConversationResumer for a profile and a conversation state.
     *
     * @param config          the plugin configuration file
     * @param conversationApi the Conversation API
     * @param onlineProfile   the profile to resume the conversation for
     * @param state           the state of a suspended conversation
     */
    public ConversationResumer(final ConfigAccessor config, final ConversationApi conversationApi,
                               final OnlineProfile onlineProfile, final PlayerConversationState state) {
        this.conversationApi = conversationApi;
        this.onlineProfile = onlineProfile;
        this.player = onlineProfile.getPlayer();
        this.state = state;
        this.distance = config.getDouble("conversation.stop.distance");
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Resumes the conversation once the player moves and is still close enough to the NPC.
     *
     * @param event a PlayerMoveEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (event.getTo().getWorld().equals(state.center().getWorld()) && event.getTo().distanceSquared(state.center()) < distance * distance) {
            HandlerList.unregisterAll(this);
            BetonQuest.getInstance().getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION, "null", onlineProfile.getProfileUUID().toString()));
            conversationApi.start(onlineProfile, state.currentConversation(), state.center(), state.currentOption());
        }
    }

    /**
     * Saves the conversation state when the player quits while already in a resumed conversation.
     *
     * @param event a PlayerQuitEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        BetonQuest.getInstance().getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION, state.toString(), onlineProfile.getProfileUUID().toString()));
    }
}
