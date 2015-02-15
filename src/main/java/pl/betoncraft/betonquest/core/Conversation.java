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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Represents a conversation between QuestPlayer and Quester
 * 
 * @author Co0sh
 */
public class Conversation implements Listener {

    /**
     * Contains a list of players in active conversation, described by their
     * playerID, and their active conversation.
     */
    private static HashMap<String, Conversation> list = new HashMap<>();
    /**
     * Represents name of the NPC
     */
    private String quester;
    /**
     * ID of the player
     */
    private final String playerID;
    /**
     * Player object for the player
     */
    private final Player player;
    /**
     * ID of the conversation
     */
    private final String conversationID;
    /**
     * Location, at which this conversation is centered
     */
    private final Location location;
    /**
     * Map containing current player answers and their pointers
     */
    private HashMap<Integer, String> current = new HashMap<Integer, String>();
    /**
     * Defines if the movement during conversation should be blocked
     */
    private boolean movementBlock;
    /**
     * Raw list of final events, separated by commas
     */
    private String finalEvents;
    /**
     * "Unknown" message said by NPC when he doesn't understand player's input
     */
    private String unknown;
    /**
     * First options for this conversation, separated by commas
     */
    private String startingOptions;

    /**
     * Constructor method, starts a new conversation between player and npc at
     * given location
     * 
     * @param playerID
     * @param conversationID
     */
    public Conversation(String playerID, String conversationID, Location location) {

        this.playerID = playerID;
        this.conversationID = conversationID;
        this.player = PlayerConverter.getPlayer(playerID);
        this.location = location;

        Debug.info("Starting conversation " + conversationID + " for player " + playerID);

        // end this conversation if it's not started properly
        if (playerID == null || conversationID == null || location == null || player == null) {
            Debug.error("Error in conversation initialization! Check your spelling!");
            return;
        }

        // if the player has active conversation, terminate this one
        if (list.containsKey(playerID)) {
            Debug.info("Player " + playerID + " is in conversation right now, returning.");
            return;
        }

        // Check if everything is defined correctly, so the conversation doesn't
        // start with invalid data
        this.quester = ConfigHandler.getString("conversations." + conversationID + ".quester");
        this.finalEvents = ConfigHandler.getString("conversations." + conversationID
            + ".final_events");
        this.unknown = ConfigHandler.getString("conversations." + conversationID + ".unknown");
        // get initial npc's options
        this.startingOptions = ConfigHandler
                .getString("conversations." + conversationID + ".first");

        // check if all data is valid (or at least exist)
        if (quester == null || quester.equals("")) {
            Debug.error("Quester's name is not defined in " + conversationID + " conversation!");
            return;
        }
        if (unknown == null || unknown.equals("")) {
            Debug.error("\"Unknown\" text is not defined in " + conversationID + " conversation!");
            return;
        }
        if (startingOptions == null || startingOptions.equals("")) {
            Debug.error("Starting options are not defined in " + conversationID + " conversation!");
            return;
        }
        // if final events are not defined then set them to none
        if (finalEvents == null) {
            finalEvents = "";
        }

        // everything is ok, register conversation as listener
        BetonQuest.getInstance().getServer().getPluginManager()
                .registerEvents(this, BetonQuest.getInstance());

        // add the player to the list of active conversations
        list.put(playerID, this);

        // print message about starting a conversation
        SimpleTextOutput.sendSystemMessage(
                playerID,
                ConfigHandler.getString(
                        "messages." + ConfigHandler.getString("config.language")
                            + ".conversation_start").replaceAll("%quester%", quester),
                ConfigHandler.getString("config.sounds.start"));

        // if stop is true stop the player from moving away
        String stop = ConfigHandler.getString("conversations." + conversationID + ".stop");
        if (stop != null && stop.equalsIgnoreCase("true")) {
            movementBlock = true;
        } else {
            movementBlock = false;
        }

        // print one of them
        printNPCText(startingOptions);
    }

    /**
     * Sends to the player the text said by NPC. It chooses the first avaliable
     * option and displays it.
     * 
     * @param options
     *            list of option pointers separated by commas
     */
    private void printNPCText(String options) {

        // if options are empty end conversation
        if (options.equals("")) {
            endConversation();
            return;
        }

        // get npc's text
        String option = null;
        options: for (String NPCoption : options.split(",")) {
            String rawConditions = ConfigHandler.getString("conversations." + this.conversationID
                + ".NPC_options." + NPCoption + ".conditions");
            if (rawConditions == null) {
                endConversation();
                Debug.error("Conversation " + conversationID
                    + " is missing conditions in NPC option " + NPCoption);
                return;
            }
            String[] conditions = rawConditions.split(",");
            for (String condition : conditions) {
                if (condition.equals("")) {
                    option = NPCoption;
                    break options;
                }
                if (!BetonQuest.condition(this.playerID, condition)) {
                    continue options;
                }
            }
            option = NPCoption;
            break;
        }

        // if there are no possible options end conversation
        if (option == null) {
            endConversation();
            return;
        }

        // and print it to player
        String text = ConfigHandler.getString("conversations." + this.conversationID
            + ".NPC_options." + option + ".text");
        if (text == null) {
            Debug.error("Conversation " + conversationID + " is missing NPC text in option "
                + option);
            endConversation();
            return;
        }
        SimpleTextOutput.sendQuesterMessage(this.playerID, quester, text);

        String events = ConfigHandler.getString("conversations." + this.conversationID
            + ".NPC_options." + option + ".events");
        if (events == null) {
            Debug.error("Conversation " + conversationID + " is missing events in NPC option "
                + option);
            endConversation();
            return;
        }
        fireEvents(events);

        String pointers = ConfigHandler.getString("conversations." + this.conversationID
            + ".NPC_options." + option + ".pointer");
        if (pointers == null) {
            Debug.error("Conversation " + conversationID + " is missing pointer in NPC option "
                + option);
            endConversation();
            return;
        }
        printOptions(pointers);
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
            SimpleTextOutput.sendQuesterMessage(playerID, quester, unknown);
            // and instructions from plugin about answering npcs
            SimpleTextOutput.sendSystemMessage(
                    playerID,
                    ConfigHandler.getString("messages."
                        + ConfigHandler.getString("config.language") + ".help_with_answering"),
                    "false");
            return;
        }

        // get the answer ID from player's response
        Integer number = new Integer(answer);
        String choosenAnswerID = current.get(number);

        // clear hashmap
        current.clear();

        // print to player his answer
        String reply = ConfigHandler.getString("conversations." + conversationID
            + ".player_options." + choosenAnswerID + ".text");
        SimpleTextOutput.sendPlayerReply(playerID, quester, reply);

        // fire events
        String events = ConfigHandler.getString("conversations." + conversationID
            + ".player_options." + choosenAnswerID + ".events");
        if (events == null) {
            Debug.error("Conversation " + conversationID + " is missing events in player option "
                + choosenAnswerID);
            endConversation();
            return;
        }
        fireEvents(events);

        // print to player npc's answer
        String NPCanswer = ConfigHandler.getString("conversations." + conversationID
            + ".player_options." + choosenAnswerID + ".pointer");
        if (NPCanswer == null) {
            Debug.error("Conversation " + conversationID + " is missing pointer in player option "
                + choosenAnswerID);
            endConversation();
            return;
        }
        printNPCText(NPCanswer);
    }

    /**
     * Fires events from the string.
     * 
     * @param rawEvents
     *            list of event IDs separated by commas
     */
    private void fireEvents(String rawEvents) {
        // do nothing if its empty
        if (!rawEvents.equalsIgnoreCase("")) {
            // split it to individual event ids
            String[] events = rawEvents.split(",");
            // foreach eventID fire an event
            for (String event : events) {
                BetonQuest.event(playerID, event);
            }
        }
    }

    /**
     * Prints answers the player can choose.
     * 
     * @param rawOptions
     *            list of pointers to player options separated by commas
     */
    private void printOptions(String rawOptions) {

        // if rawOptions are empty
        if (rawOptions.equals("")) {
            endConversation();
            return;
        }

        // get IDs
        String[] options = rawOptions.split(",");

        // print them
        int i = 0;
        answers: for (String option : options) {
            // get conditions from config
            String rawConditions = ConfigHandler.getString("conversations." + conversationID
                + ".player_options." + option + ".conditions");
            if (rawConditions == null) {
                Debug.error("Conversation " + conversationID
                    + " is missing conditions in player option " + option);
                endConversation();
                return;
            }
            // if there are any conditions, do something with them
            if (!rawConditions.equalsIgnoreCase("")) {
                // split them to separate ids
                String[] conditions = rawConditions.split(",");
                // if some condition is not met, skip printing this option and
                // move on
                for (String conditionID : conditions) {
                    if (!BetonQuest.condition(playerID, conditionID)) {
                        continue answers;
                    }
                }
            }
            // i is for counting replies, like 1. something, 2. something else
            // etc.
            i++;
            // print reply
            String reply = ConfigHandler.getString("conversations." + conversationID
                + ".player_options." + option + ".text");
            if (reply == null) {
                Debug.error("Conversation " + conversationID + " is missing text in player option "
                    + option);
                endConversation();
                return;
            }
            SimpleTextOutput.sendQuesterReply(playerID, i, quester, reply);
            // put reply to hashmap in order to find it's ID when player
            // responds by
            // it's i number (id is string, we don't want to print it to player)
            current.put(Integer.valueOf(i), option);
        }

        // end conversations if there are no possible options
        if (current.isEmpty()) {
            endConversation();
            return;
        }
    }

    /**
     * Ends conversation, firing final events and removing it from the list of
     * active conversations
     */
    public void endConversation() {
        // fire final events
        if (!finalEvents.equals("")) {
            String[] splitFinalEvents = finalEvents.split(",");
            for (String event : splitFinalEvents) {
                BetonQuest.event(playerID, event);
            }
        }
        // print message
        SimpleTextOutput.sendSystemMessage(
                playerID,
                ConfigHandler.getString(
                        "messages." + ConfigHandler.getString("config.language")
                            + ".conversation_end").replaceAll("%quester%", quester),
                ConfigHandler.getString("config.sounds.end"));
        // delete conversation on the next tick to prevent errors
        new BukkitRunnable() {
            @Override
            public void run() {
                list.remove(playerID);
            }
        }.runTask(BetonQuest.getInstance());
        // unregister listener
        HandlerList.unregisterAll(this);
    }

    /**
     * Checks if the movement of the player should be blocked.
     * 
     * @return true if the movement should be blocked, false otherwise
     */
    public boolean isMovementBlock() {
        return movementBlock;
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
            // processing the answer should be done in a sync thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    passPlayerAnswer(event.getMessage());
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    @EventHandler
    public void onWalkAway(PlayerMoveEvent event) {
        // return if it's someone else
        if (!event.getPlayer().equals(player)) {
            return;
        }
        // if player passes max distance
        if (!event.getTo().getWorld().equals(location.getWorld())
            || event.getTo().distance(location) > Integer.valueOf(ConfigHandler
                    .getString("config.max_npc_distance"))) {
            // we can stop the player or end conversation
            if (isMovementBlock()) {
                moveBack(event);
            } else {
                endConversation();
            }
        }
        return;
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
        if (!event.getTo().getWorld().equals(location.getWorld())
            || event.getTo().distance(location) > Integer.valueOf(ConfigHandler
                    .getString("config.max_npc_distance")) * 2) {
            event.getPlayer().teleport(location);
            return;
        }
        // if not, then calculate the vector
        float yaw = event.getTo().getYaw();
        float pitch = event.getTo().getPitch();
        Vector vector = new Vector(location.getX() - event.getTo().getX(), location.getY()
            - event.getTo().getY(), location.getZ() - event.getTo().getZ());
        vector = vector.multiply(1 / vector.length());
        // and teleport him back using this vector
        Location newLocation = event.getTo().clone();
        newLocation.add(vector);
        newLocation.setPitch(pitch);
        newLocation.setYaw(yaw);
        event.getPlayer().teleport(newLocation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // if player quits, end conversation (why keep listeners running?)
        if (event.getPlayer().equals(player)) {
            endConversation();
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
            if (list.containsKey(playerID))
                list.get(playerID).endConversation();
        }
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

}
