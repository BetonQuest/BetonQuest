package org.betonquest.betonquest.conversation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
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
@SuppressWarnings("PMD.CommentRequired")
public class ConversationResumer implements Listener {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    private final PlayerConversationState state;
    private final Player player;

    private final OnlineProfile onlineProfile;
    private final double distance;

    /**
     * Creates a new ConversationResumer for a profile and a conversation state.
     *
     * @param onlineProfile the profile to resume the conversation for
     * @param state         the state of a suspended conversation
     */
    public ConversationResumer(final BetonQuestLoggerFactory loggerFactory, final OnlineProfile onlineProfile, final PlayerConversationState state) {
        this.loggerFactory = loggerFactory;
        this.onlineProfile = onlineProfile;
        this.player = onlineProfile.getPlayer();
        this.state = state;
        this.distance = Double.parseDouble(Config.getString("config.max_npc_distance"));
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Resumes the conversation once the player moves and is still close enough to the NPC.
     *
     * @param event a PlayerMoveEvent
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (event.getTo().getWorld().equals(state.location().getWorld()) && event.getTo().distanceSquared(state.location()) < distance * distance) {
            HandlerList.unregisterAll(this);
            BetonQuest.getInstance().getSaver()
                    .add(new Record(UpdateType.UPDATE_CONVERSATION, "null", onlineProfile.getProfileUUID().toString()));
            new Conversation(loggerFactory.create(Conversation.class), onlineProfile, state.currentConversation(), state.location(), state.currentOption());
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
        BetonQuest.getInstance().getSaver()
                .add(new Record(UpdateType.UPDATE_CONVERSATION, state.toString(), onlineProfile.getProfileUUID().toString()));
    }
}
