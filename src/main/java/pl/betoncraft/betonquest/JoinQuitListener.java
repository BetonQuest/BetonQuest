package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.ConversationResumer;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.File;
import java.util.logging.Level;

/**
 * Listener which handles data loadin/saving when players are joining/quitting
 */
public class JoinQuitListener implements Listener {
    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        // if player was kicked, don't load the data
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final String playerID = event.getUniqueId().toString();
        final BetonQuest plugin = BetonQuest.getInstance();
        plugin.putPlayerData(playerID, new PlayerData(playerID));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        // start objectives when the data is loaded
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
        // if the data still isn't loaded, force loading (this happens sometimes
        // probably because AsyncPlayerPreLoginEvent does not fire)
        if (playerData == null) {
            playerData = new PlayerData(playerID);
            BetonQuest.getInstance().putPlayerData(playerID, playerData);
            LogUtils.getLogger().log(Level.WARNING, "Failed to load data for player " + event.getPlayer().getName() + ", forcing.");
        }
        playerData.startObjectives();
        GlobalObjectives.startAll(playerID);
        // display changelog message to the admins
        if (event.getPlayer().hasPermission("betonquest.admin")
                && new File(BetonQuest.getInstance().getDataFolder(), "CHANGELOG.md").exists()) {
            Config.sendNotify(PlayerConverter.getID(event.getPlayer()), "changelog", null, "changelog,info");
        }
        if (Journal.hasJournal(playerID)) {
            playerData.getJournal().update();
        }
        if (playerData.getConversation() != null) {
            new ConversationResumer(playerID, playerData.getConversation());
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
            objective.removePlayer(playerID);
        }
        BetonQuest.getInstance().removePlayerData(playerID);
    }
}
