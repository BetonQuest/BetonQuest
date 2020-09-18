package pl.betoncraft.betonquest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Resumes the conversation for the player
 */
public class ConversationResumer implements Listener {

    private final String original;
    private final Player player;
    private final String playerID;
    private final String conversationID;
    private final String option;
    private Location loc;
    private double distance;

    public ConversationResumer(final String playerID, final String convID) {
        this.original = convID;
        this.player = PlayerConverter.getPlayer(playerID);
        this.playerID = playerID;
        final String[] parts = convID.split(" ");
        this.conversationID = parts[0];
        this.option = parts[1];
        if (option.equalsIgnoreCase("null")) {
            return;
        }
        final String[] locParts = parts[2].split(";");
        this.loc = new Location(Bukkit.getWorld(locParts[3]), Double.parseDouble(locParts[0]),
                Double.parseDouble(locParts[1]), Double.parseDouble(locParts[2]));
        this.distance = Double.valueOf(Config.getString("config.max_npc_distance"));
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (event.getTo().getWorld().equals(loc.getWorld())) {
            if (event.getTo().distanceSquared(loc) < distance * distance) {
                HandlerList.unregisterAll(this);
                BetonQuest.getInstance().getSaver()
                        .add(new Record(UpdateType.UPDATE_CONVERSATION, new String[]{"null", playerID}));
                new Conversation(playerID, conversationID, loc, option);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        BetonQuest.getInstance().getSaver()
                .add(new Record(UpdateType.UPDATE_CONVERSATION, new String[]{original, playerID}));
    }
}
