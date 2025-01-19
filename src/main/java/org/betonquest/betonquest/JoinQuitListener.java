package org.betonquest.betonquest;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.objective.ResourcePackObjective;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Listener which handles data loading/saving when players are joining/quitting.
 */
public class JoinQuitListener implements Listener {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Stores the {@link PlayerData}.
     */
    private final BetonQuest betonQuest;

    /**
     * Holds loaded PlayerData.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Creates new listener, which will handle the data loading/saving.
     *
     * @param loggerFactory     used for logger creation in ConversationResumer
     * @param betonQuest        the object to store and remove {@link PlayerData}
     * @param playerDataStorage the storage for un-/loading player data
     */
    public JoinQuitListener(final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest, final PlayerDataStorage playerDataStorage) {
        this.loggerFactory = loggerFactory;
        this.betonQuest = betonQuest;
        this.playerDataStorage = playerDataStorage;
    }

    /**
     * Loads the player data async before it joins.
     *
     * @param event the async event to listen
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final Profile profile = PlayerConverter.getID(Bukkit.getOfflinePlayer(event.getUniqueId()));
        playerDataStorage.put(profile, new PlayerData(profile));
    }

    /**
     * Starts the player objectives and running conversation on join.
     *
     * @param event the join event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        final PlayerData playerData = playerDataStorage.get(onlineProfile);
        playerData.startObjectives();
        GlobalObjectives.startAll(onlineProfile, playerDataStorage);
        checkResourcepack(event, onlineProfile);

        if (Journal.hasJournal(onlineProfile)) {
            playerData.getJournal().update();
        }
        if (playerData.getActiveConversation() != null) {
            new ConversationResumer(loggerFactory, onlineProfile, playerData.getActiveConversation());
        }
    }

    private void checkResourcepack(final PlayerJoinEvent event, final OnlineProfile onlineProfile) {
        if (!PaperLib.isPaper()) {
            return;
        }
        final PlayerResourcePackStatusEvent.Status resourcePackStatus = event.getPlayer().getResourcePackStatus();
        if (resourcePackStatus != null) {
            betonQuest.getPlayerObjectives(onlineProfile).stream()
                    .filter(objective -> objective instanceof ResourcePackObjective)
                    .map(objective -> (ResourcePackObjective) objective)
                    .forEach(objective -> objective.processObjective(onlineProfile, resourcePackStatus));
        }
    }

    /**
     * Removes the PlayerData from storage when the player quits the server.
     *
     * @param event the quit event
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        for (final Objective objective : betonQuest.getPlayerObjectives(onlineProfile)) {
            objective.pauseObjectiveForPlayer(onlineProfile);
        }
        playerDataStorage.remove(onlineProfile);
    }
}
