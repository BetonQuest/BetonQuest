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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.ConversationData.OptionType;
import pl.betoncraft.betonquest.core.ConversationData.RequestType;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Represents a conversation between player and NPC
 * 
 * @author Jakub Sapalski
 */
public class Conversation implements Listener {

    private static ConcurrentHashMap<String, Conversation> list =
            new ConcurrentHashMap<>();
    
    private final String playerID;
    private final Player player;
    private final String language;
    private final ConversationData data;
    private final Location location;
    private final List<String> blacklist;
    private ConversationIO inOut;
    
    private HashMap<Integer, String> current = new HashMap<>();

    /**
     * Starts a new conversation between player and npc at given location
     * 
     * @param playerID
     *          ID of the player
     * @param packName
     *          name of the package in which this conversation is defined
     * @param conversationID
     *          name of the conversation
     * @param location
     *          location where the conversation has been started
     */
    public Conversation(final String playerID, final String packName,
            final String conversationID, final Location location) {

        this.playerID = playerID;
        this.player = PlayerConverter.getPlayer(playerID);
        this.language = BetonQuest.getInstance().getDBHandler(playerID).getLanguage();
        this.location = location;
        this.data = BetonQuest.getInstance().getConversation(
                packName + "." + conversationID);
        this.blacklist = BetonQuest.getInstance().getConfig()
                .getStringList("cmd_blacklist");
        
        // check if data is present
        if (data == null) {
            Debug.error("Conversation doesn't exist: " + packName + "."
                    + conversationID);
            return;
        }
        
        // if the player has active conversation, terminate this one
        if (list.containsKey(playerID)) {
            Debug.info("Player " + PlayerConverter.getName(playerID) +
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
                
                // now the conversation should start no matter what
                // the inOut can be safely instantiated; doing it before
                // would leave it active while the conversation is not
                // started, causing it to display "null" all the time
                try {
                    String name = BetonQuest.getInstance().getConfig()
                            .getString("default_conversation_IO");
                    Class<? extends ConversationIO> c = BetonQuest.getInstance()
                            .getConvIO(name);
                    if (c == null) {
                        Debug.error("Conversation IO " + name + " is not registered!");
                        return;
                    }
                    conv.inOut = c.getConstructor(Conversation.class, String.class, String.class)
                            .newInstance(conv, playerID, data.getQuester(language));
                } catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                    Debug.error("Error when loading conversation IO");
                    return;
                }

                // add the player to the list of active conversations
                list.put(playerID, conv);
                
                // register listeners for immunity and blocking commands
                Bukkit.getPluginManager().registerEvents(conv, BetonQuest
                        .getInstance());

                // print message about starting a conversation
                Config.sendMessage(playerID, "conversation_start",
                        new String[]{data.getQuester(language)}, "start");
                
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
        inOut.setNPCResponse(data.getText(language, option, OptionType.NPC));

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
    public void passPlayerAnswer(int number) {
        
        inOut.clear();

        // get the answer ID from player's response
        final String choosenAnswerID = current.get(number);

        // clear hashmap
        current.clear();

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
            // print reply and put it to the hashmap
            current.put(Integer.valueOf(i), option);
            inOut.addPlayerOption(data.getText(language, option, OptionType.PLAYER));
        }
        inOut.display();
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
        inOut.end();
        // fire final events
        for (String event : data.getFinalEvents()) {
            BetonQuest.event(playerID, event);
        }
        // print message
        Config.sendMessage(playerID, "conversation_end",
                new String[]{data.getQuester(language)}, "end");
        // delete conversation
        list.remove(playerID);
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
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (event.getMessage() == null) return;
        String cmdName = event.getMessage().split(" ")[0].substring(1);
        if (blacklist.contains(cmdName)) {
            event.setCancelled(true);
            Config.sendMessage(PlayerConverter.getID(event.getPlayer()), "command_blocked");
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // prevent damage to (or from) player while in conversation
        if ((event.getEntity() instanceof Player && PlayerConverter.getID(
                (Player) event.getEntity()).equals(playerID))
                || (event.getDamager() instanceof Player && PlayerConverter
                        .getID((Player) event.getDamager()).equals(playerID))) {
            event.setCancelled(true);
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
                list.get(playerID).endConversation();
                Bukkit.getServer().getPluginManager().callEvent(
                        new PlayerConversationEndEvent(player, list.get(playerID)));
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

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

}
