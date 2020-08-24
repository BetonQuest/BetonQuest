/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 *
 * @author Jakub Sapalski
 */
public class ConversationResumer implements Listener {

    private String original;
    private Player player;
    private String playerID;
    private String conversationID;
    private String option;
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
