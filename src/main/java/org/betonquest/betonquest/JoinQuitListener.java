package org.betonquest.betonquest;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.objectives.ResourcePackObjective;
import org.betonquest.betonquest.utils.PlayerConverter;
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
@SuppressWarnings("PMD.CommentRequired")
public class JoinQuitListener implements Listener {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates new listener, which will handle the data loading/saving.
     *
     * @param loggerFactory used for logger creation in ConversationResumer
     */
    public JoinQuitListener(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final Profile profile = PlayerConverter.getID(Bukkit.getOfflinePlayer(event.getUniqueId()));
        final BetonQuest plugin = BetonQuest.getInstance();
        plugin.putPlayerData(profile, new PlayerData(profile));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
        playerData.startObjectives();
        GlobalObjectives.startAll(onlineProfile);
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
            BetonQuest.getInstance().getPlayerObjectives(onlineProfile).stream()
                    .filter(objective -> objective instanceof ResourcePackObjective)
                    .map(objective -> (ResourcePackObjective) objective)
                    .forEach(objective -> objective.processObjective(onlineProfile, resourcePackStatus));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(onlineProfile)) {
            objective.pauseObjectiveForPlayer(onlineProfile);
        }
        BetonQuest.getInstance().removePlayerData(onlineProfile);
    }
}
