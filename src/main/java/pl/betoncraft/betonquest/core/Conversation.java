/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.core;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.ConversationData.OptionType;
import pl.betoncraft.betonquest.core.ConversationData.RequestType;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Represents a conversation between QuestPlayer and Quester
 * 
 * @author Co0sh
 */
public class Conversation implements Listener {

    private static ConcurrentHashMap<String, Conversation> list =
            new ConcurrentHashMap<>();
    
    private final String playerID;
    private final Player player;
    private final ConversationData data;
    private final Location location;
    private HashMap<Integer, String> current = new HashMap<>();
    private HashMap<Integer, String> hashes = new HashMap<>();
    private boolean tellraw;

    /**
     * Constructor method, starts a new conversation between player and npc at
     * given location
     * 
     * @param playerID
     * @param conversationID
     */
    public Conversation(final String playerID, final String packName,
            final String conversationID, final Location location) {

        this.playerID = playerID;
        this.player = PlayerConverter.getPlayer(playerID);
        this.location = location;
        this.tellraw = Config.getString("config.tellraw")
                .equalsIgnoreCase("true");
        this.data = BetonQuest.getInstance().getConversation(
                packName + "." + conversationID);
        
        // check if data is present
        if (data == null) {
            Debug.error("Conversation doesn't exist: " + packName + "."
                    + conversationID);
            return;
        }
        
        // if the player has active conversation, terminate this one
        if (list.containsKey(playerID)) {
            Debug.info("Player " + playerID +
                    " is in conversation right now, returning.");
            return;
        }
        
        // start the conversation asynchronously
        final Conversation conv = this;
        new BukkitRunnable() {
            public void run() {
                
                // the conversation start event must be run on next tick
                PlayerConversationStartEvent event =
                        new PlayerConversationStartEvent(player, conv);
                Bukkit.getServer().getPluginManager().callEvent(event);
                
                // stop the conversation if it's canceled
                if (event.isCancelled()) return;
                
                // everything is ok, register conversation as listener
                BetonQuest.getInstance().getServer().getPluginManager()
                        .registerEvents(conv, BetonQuest.getInstance());

                // add the player to the list of active conversations
                list.put(playerID, conv);

                // print message about starting a conversation
                SimpleTextOutput.sendSystemMessage(playerID,
                        Config.getMessage("conversation_start")
                        .replaceAll("%quester%", data.getQuester()),
                        Config.getString("config.sounds.start"));
                
                // print NPC's text
                printNPCText(data.getStartingOptions());
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
    }

    /**
     * Sends to the player the text said by NPC. It chooses the first avaliable
     * option and displays it.
     * 
     * @param options
     *            list of option pointers separated by commas
     */
    private void printNPCText(String[] options) {

        // get npc's text
        String option = null;
        options: for (String NPCoption : options) {
            for (String condition : data.getData(NPCoption, OptionType.NPC,
                    RequestType.CONDITION)) {
                if (!BetonQuest.condition(this.playerID, condition)) {
                    continue options;
                }
            }
            option = NPCoption;
            break;
        }

        // if there are no possible options, end conversation
        if (option == null) {
            new BukkitRunnable() {
                public void run() {
                    endConversation();
                }
            }.runTask(BetonQuest.getInstance());
            return;
        }

        // print option to the player
        SimpleTextOutput.sendQuesterMessage(this.playerID, data.getQuester(),
                data.getText(option, OptionType.NPC));

        final String fOption = option;
        new BukkitRunnable() {
            public void run() {
                // fire events
                for (String event : data.getData(fOption, OptionType.NPC,
                        RequestType.EVENT)) {
                    BetonQuest.event(playerID, event);
                }
                new BukkitRunnable() {
                    public void run() {
                        // print options
                        printOptions(data.getData(fOption, OptionType.NPC,
                                RequestType.POINTER));
                    }
                }.runTaskAsynchronously(BetonQuest.getInstance());
            }
        }.runTask(BetonQuest.getInstance());
    }

    /**
     * Passes given string as answer from player in a conversation.
     * 
     * @param rawAnswer
     *            the message player has sent on chat
     */
    public void passPlayerAnswer(String rawAnswer) {

        String answer = rawAnswer.trim();

        // if answer isn't a number, or the number is greater than amount of
        // possible options then print messages
        if (answer.equalsIgnoreCase("0") || !answer.matches("\\d+")
            || Integer.valueOf(answer) > current.size()) {
            // some text from npc saying that he doesn't understand player
            SimpleTextOutput.sendQuesterMessage(playerID, data.getQuester(),
                    data.getUnknown());
            // and instructions from plugin about answering npcs
            SimpleTextOutput.sendSystemMessage(playerID,
                    Config.getMessage("help_with_answering"), "false");
            return;
        }

        // get the answer ID from player's response
        Integer number = new Integer(answer);
        final String choosenAnswerID = current.get(number);

        // clear hashmap
        current.clear();
        if (tellraw) hashes.clear();

        // print to player his answer
        SimpleTextOutput.sendPlayerReply(playerID, data.getQuester(),
                data.getText(choosenAnswerID, OptionType.PLAYER));

        new BukkitRunnable() {
            public void run() {
                // fire events
                for (String event : data.getData(choosenAnswerID,
                        OptionType.PLAYER, RequestType.EVENT)) {
                    BetonQuest.event(playerID, event);
                }
                new BukkitRunnable() {
                    public void run() {
                        // print to player npc's answer
                        printNPCText(data.getData(choosenAnswerID,
                                OptionType.PLAYER, RequestType.POINTER));
                    }
                }.runTaskAsynchronously(BetonQuest.getInstance());
            }
        }.runTask(BetonQuest.getInstance());
    }

    /**
     * Prints answers the player can choose.
     * 
     * @param options
     *            list of pointers to player options separated by commas
     */
    private void printOptions(String[] options) {
        // i is for counting replies, like 1. something, 2. something else
        int i = 0;
        answers: for (String option : options) {
            for (String condition : data.getData(option, OptionType.PLAYER,
                    RequestType.CONDITION)) {
                if (!BetonQuest.condition(playerID, condition)) {
                    continue answers;
                }
            }
            i++;
            // print reply
            String randomID = UUID.randomUUID().toString();
            SimpleTextOutput.sendQuesterReply(playerID, i, data.getQuester(),
                    data.getText(option, OptionType.PLAYER), randomID);
            // put reply to hashmap
            current.put(Integer.valueOf(i), option);
            if (tellraw) {
                hashes.put(Integer.valueOf(i), randomID);
            }
        }
        // end conversations if there are no possible options
        if (current.isEmpty()) {
            new BukkitRunnable() {
                public void run() {
                    endConversation();
                }
            }.runTask(BetonQuest.getInstance());
            return;
        }
    }

    /**
     * Ends conversation, firing final events and removing it from the list of
     * active conversations
     */
    public void endConversation() {
        // fire final events
        for (String event : data.getFinalEvents()) {
            BetonQuest.event(playerID, event);
        }
        // print message
        SimpleTextOutput.sendSystemMessage(playerID, Config.getMessage(
                "conversation_end").replaceAll("%quester%", data.getQuester()),
                Config.getString("config.sounds.end"));
        // delete conversation
        list.remove(playerID);
        // unregister listener
        HandlerList.unregisterAll(this);
        Bukkit.getServer().getPluginManager().callEvent(
                new PlayerConversationEndEvent(player, this));
    }

    /**
     * Checks if the movement of the player should be blocked.
     * 
     * @return true if the movement should be blocked, false otherwise
     */
    public boolean isMovementBlock() {
        return data.isMovementBlocked();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onReply(final AsyncPlayerChatEvent event) {
        // return if it's someone else
        if (event.getPlayer() != player) {
            return;
        }
        if (event.getMessage().startsWith("#")) {
            event.setMessage(event.getMessage().substring(1).trim());
        } else {
            event.setCancelled(true);
            passPlayerAnswer(event.getMessage());
        }
    }

    @EventHandler
    public void onWalkAway(PlayerMoveEvent event) {
        // return if it's someone else
        if (!event.getPlayer().equals(player)) {
            return;
        }
        // if player passes max distance
        if (!event.getTo().getWorld().equals(location.getWorld()) ||
                event.getTo().distance(location) > Integer.valueOf(
                        Config.getString("config.max_npc_distance"))) {
            // we can stop the player or end conversation
            if (isMovementBlock()) {
                moveBack(event);
            } else {
                endConversation();
            }
        }
        return;
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // prevent damage to (or from) player while in conversation
        if ((event.getEntity() instanceof Player && PlayerConverter.getID(
                (Player) event.getEntity()).equals(playerID)) ||
                (event.getDamager() instanceof Player && PlayerConverter
                        .getID((Player) event.getDamager()).equals(playerID))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // if player quits, end conversation (why keep listeners running?)
        if (event.getPlayer().equals(player)) {
            // delete conversation
            list.remove(playerID);
            // unregister listener
            HandlerList.unregisterAll(this);
            Bukkit.getServer().getPluginManager().callEvent(
                    new PlayerConversationEndEvent(player, this));
        }
    }

    /**
     * Moves the player back a few blocks in the conversation's center
     * direction.
     * 
     * @param event
     *            PlayerMoveEvent event, for extracting the necessary data
     */
    private void moveBack(PlayerMoveEvent event) {
        // if the player is in other world (he teleported himself), teleport him
        // back to the center of the conversation
        if (!event.getTo().getWorld().equals(location.getWorld()) ||
                event.getTo().distance(location) > Integer.valueOf(
                        Config.getString("config.max_npc_distance")) * 2) {
            event.getPlayer().teleport(location);
            return;
        }
        // if not, then calculate the vector
        float yaw = event.getTo().getYaw();
        float pitch = event.getTo().getPitch();
        Vector vector = new Vector(location.getX() - event.getTo().getX(),
                location.getY() - event.getTo().getY(), location.getZ()
                - event.getTo().getZ());
        vector = vector.multiply(1 / vector.length());
        // and teleport him back using this vector
        Location newLocation = event.getTo().clone();
        newLocation.add(vector);
        newLocation.setPitch(pitch);
        newLocation.setYaw(yaw);
        event.getPlayer().teleport(newLocation);
        if (Config.getString("config.notify_pullback").equalsIgnoreCase("true")) {
            event.getPlayer().sendMessage(Config.getMessage("pullback")
                    .replaceAll("&", "§"));
        }
    }

    /**
     * Checks if the player is in a conversation
     * 
     * @param playerID
     *            ID of the player
     * @return if the player is on the list of active conversations
     */
    public static boolean containsPlayer(String playerID) {
        return list.containsKey(playerID);
    }

    /**
     * Ends every active conversation for every online player.
     */
    public static void clear() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerID = PlayerConverter.getID(player);
            if (list.containsKey(playerID)) {
                Conversation conv = list.get(playerID);
                // unregister listener
                HandlerList.unregisterAll(conv);
                Bukkit.getServer().getPluginManager().callEvent(
                        new PlayerConversationEndEvent(player, conv));
            }
        }
        list.clear();
    }

    /**
     * Gets this player's active conversation.
     * 
     * @param playerID
     *            ID of the player
     * @return player's active conversation or null if there is no conversation
     */
    public static Conversation getConversation(String playerID) {
        return list.get(playerID);
    }
    
    public HashMap<Integer, String> getHashes() {
        return hashes;
    }

}
