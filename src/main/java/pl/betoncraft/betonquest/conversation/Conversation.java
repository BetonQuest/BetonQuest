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

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.ConversationOptionEvent;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.conversation.ConversationData.OptionType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Represents a conversation between player and NPC
 *
 * @author Jakub Sapalski
 */
public class Conversation implements Listener {

    private static ConcurrentHashMap<String, Conversation> list = new ConcurrentHashMap<>();

    private final String playerID;
    private final Player player;
    private final ConfigPackage pack;
    private final String language;
    private final Location location;
    private final String convID;
    private final List<String> blacklist;
    private final Conversation conv;
    private final BetonQuest plugin;
    private ConversationData data;
    private ConversationIO inOut;
    private String option;
    private boolean ended = false;
    private boolean messagesDelaying = false;
    private Interceptor interceptor;

    private HashMap<Integer, String> current = new HashMap<>();


    /**
     * Starts a new conversation between player and npc at given location. It uses
     * starting options to determine where to start.
     *
     * @param playerID       ID of the player
     * @param conversationID ID of the conversation
     * @param location       location where the conversation has been started
     */
    public Conversation(final String playerID, final String conversationID, final Location location) {
        this(playerID, conversationID, location, null);
    }

    /**
     * Starts a new conversation between player and npc at given location,
     * starting with the given option. If the option is null, then it will start
     * from the beginning.
     *
     * @param playerID       ID of the player
     * @param conversationID ID of the conversation
     * @param location       location where the conversation has been started
     * @param option         ID of the option from where to start
     */
    public Conversation(final String playerID, final String conversationID,
                        final Location location, String option) {

        this.conv = this;
        this.plugin = BetonQuest.getInstance();
        this.playerID = playerID;
        this.player = PlayerConverter.getPlayer(playerID);
        this.pack = Config.getPackages().get(conversationID.substring(0, conversationID.indexOf('.')));
        this.language = plugin.getPlayerData(playerID).getLanguage();
        this.location = location;
        this.convID = conversationID;
        this.data = plugin.getConversation(convID);
        this.blacklist = plugin.getConfig().getStringList("cmd_blacklist");
        this.messagesDelaying = plugin.getConfig().getString("display_chat_after_conversation").equalsIgnoreCase("true");

        // check if data is present
        if (data == null) {
            LogUtils.getLogger().log(Level.WARNING, "Conversation doesn't exist: " + conversationID);
            return;
        }

        // if the player has active conversation, terminate this one
        if (list.containsKey(playerID)) {
            LogUtils.getLogger().log(Level.FINE, "Player " + PlayerConverter.getName(playerID) + " is in conversation right now, returning.");
            return;
        }

        // add the player to the list of active conversations
        list.put(playerID, conv);

        final String[] options;
        if (option == null) {
            options = null;
        } else {
            if (!option.contains(".")) {
                option = conversationID.substring(conversationID.indexOf('.') + 1) + "." + option;
            }
            options = new String[]{option};
        }

        new Starter(options).runTaskAsynchronously(BetonQuest.getInstance());
    }

    /**
     * Checks if the player is in a conversation
     *
     * @param playerID ID of the player
     * @return if the player is on the list of active conversations
     */
    public static boolean containsPlayer(final String playerID) {
        return list.containsKey(playerID);
    }

    /**
     * Gets this player's active conversation.
     *
     * @param playerID ID of the player
     * @return player's active conversation or null if there is no conversation
     */
    public static Conversation getConversation(final String playerID) {
        return list.get(playerID);
    }

    /**
     * Chooses the first available option.
     *
     * @param options list of option pointers separated by commas
     * @param force   setting it to true will force the first option, even if
     *                conditions are not met
     */
    private void selectOption(String[] options, final boolean force) {

        if (force) {
            options = new String[]{options[0]};
        }
        // get npc's text
        option = null;
        options:
        for (final String option : options) {
            final String convName;
            final String optionName;
            if (option.contains(".")) {
                final String[] parts = option.split("\\.");
                convName = parts[0];
                optionName = parts[1];
            } else {
                convName = data.getName();
                optionName = option;
            }
            final ConversationData currentData = plugin.getConversation(pack.getName() + "." + convName);
            if (!force) {
                for (final ConditionID condition : currentData.getConditionIDs(optionName, OptionType.NPC)) {
                    if (!BetonQuest.condition(this.playerID, condition)) {
                        continue options;
                    }
                }
            }
            this.option = optionName;
            data = currentData;
            break;
        }
    }

    /**
     * Sends to the player the text said by NPC. It uses the selected option and
     * displays it. Note: this method now requires a prior call to
     * selectOption()
     */
    private void printNPCText() {

        // if there are no possible options, end conversation
        if (option == null) {
            new ConversationEnder().runTask(BetonQuest.getInstance());
            return;
        }
        String text = data.getText(playerID, language, option, OptionType.NPC);
        // resolve variables
        for (final String variable : BetonQuest.resolveVariables(text)) {
            text = text.replace(variable, plugin.getVariableValue(data.getPackName(), variable, playerID));
        }
        // print option to the player
        inOut.setNpcResponse(data.getQuester(language), text);

        new NPCEventRunner(option).runTask(BetonQuest.getInstance());
    }

    /**
     * Passes given string as answer from player in a conversation.
     *
     * @param number the message player has sent on chat
     */
    public void passPlayerAnswer(final int number) {

        inOut.clear();

        new PlayerEventRunner(current.get(number)).runTask(BetonQuest.getInstance());

        // clear hashmap
        current.clear();
    }

    /**
     * Prints answers the player can choose.
     *
     * @param options list of pointers to player options separated by commas
     */
    private void printOptions(final String[] options) {
        // i is for counting replies, like 1. something, 2. something else
        int optionsCount = 0;
        answers:
        for (final String option : options) {
            for (final ConditionID condition : data.getConditionIDs(option, OptionType.PLAYER)) {
                if (!BetonQuest.condition(playerID, condition)) {
                    continue answers;
                }
            }
            optionsCount++;
            // print reply and put it to the hashmap
            current.put(optionsCount, option);
            // replace variables with their values
            String text = data.getText(playerID, language, option, OptionType.PLAYER);
            for (final String variable : BetonQuest.resolveVariables(text)) {
                text = text.replace(variable, plugin.getVariableValue(data.getPackName(), variable, playerID));
            }
            inOut.addPlayerOption(text);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                inOut.display();
            }
        }.runTask(BetonQuest.getInstance());
        // end conversations if there are no possible options
        if (current.isEmpty()) {
            new ConversationEnder().runTask(BetonQuest.getInstance());
            return;
        }
    }

    /**
     * Ends conversation, firing final events and removing it from the list of
     * active conversations
     */
    public void endConversation() {
        if (ended) {
            return;
        }
        ended = true;
        inOut.end();
        // fire final events
        for (final EventID event : data.getFinalEvents()) {
            BetonQuest.event(playerID, event);
        }
        //only display status messages if conversationIO allows it
        if (conv.inOut.printMessages()) {
            // print message
            conv.inOut.print(Config.parseMessage(playerID, "conversation_end", new String[]{data.getQuester(language)}));
        }
        //play conversation end sound
        Config.playSound(playerID, "end");

        // End interceptor after a second
        if (interceptor != null) {
            interceptor.end();
        }

        // delete conversation
        list.remove(playerID);
        HandlerList.unregisterAll(this);

        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(player, Conversation.this));
            }
        }.runTask(BetonQuest.getInstance());
    }

    /**
     * @return whenever this conversation has already ended
     */
    public boolean isEnded() {
        return ended;
    }

    /**
     * Send message to player, bypassing any message delaying if needed
     */
    public void sendMessage(final String message) {
        if (interceptor == null) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(message));
        } else {
            interceptor.sendMessage(message);
        }
    }

    public void sendMessage(final BaseComponent[] message) {
        if (interceptor == null) {
            player.spigot().sendMessage(message);
        } else {
            interceptor.sendMessage(message);
        }
    }

    /**
     * Checks if the movement of the player should be blocked.
     *
     * @return true if the movement should be blocked, false otherwise
     */
    public boolean isMovementBlock() {
        return data.isMovementBlocked();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (event.getMessage() == null) {
            return;
        }
        final String cmdName = event.getMessage().split(" ")[0].substring(1);
        if (blacklist.contains(cmdName)) {
            event.setCancelled(true);

            Config.sendNotify(PlayerConverter.getID(event.getPlayer()), "command_blocked", "command_blocked,error");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        // prevent damage to (or from) player while in conversation
        if (event.getEntity() instanceof Player && PlayerConverter.getID((Player) event.getEntity()).equals(playerID)
                || event.getDamager() instanceof Player
                && PlayerConverter.getID((Player) event.getDamager()).equals(playerID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        // if player quits, end conversation (why keep listeners running?)
        if (event.getPlayer().equals(player)) {
            if (isMovementBlock()) {
                suspend();
            } else {
                endConversation();
            }
        }
    }

    /**
     * Instead of ending the conversation it saves it to the database, from
     * where it will be resumed after the player logs in again.
     */
    public void suspend() {
        if (inOut == null) {
            LogUtils.getLogger().log(Level.WARNING, "Conversation IO is not loaded, conversation will end for player "
                    + PlayerConverter.getName(playerID));
            list.remove(playerID);
            HandlerList.unregisterAll(this);
            return;
        }
        inOut.end();

        // save the conversation to the database
        final String loc = location.getX() + ";" + location.getY() + ";" + location.getZ() + ";"
                + location.getWorld().getName();
        plugin.getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION,
                new String[]{convID + " " + option + " " + loc, playerID}));

        // End interceptor
        if (interceptor != null) {
            interceptor.end();
        }

        // delete conversation
        list.remove(playerID);
        HandlerList.unregisterAll(this);

        try {
            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(player, Conversation.this));
                }
            }.runTask(BetonQuest.getInstance());
        } catch (IllegalPluginAccessException e) {
            LogUtils.logThrowableIgnore(e);
        }

    }

    /**
     * @return the location where the conversation has been started
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return the ConversationIO object used by this conversation
     */
    public ConversationIO getIO() {
        return inOut;
    }

    /**
     * @return the data of the conversation
     */
    public ConversationData getData() {
        return data;
    }

    /**
     * @return the package containing this conversation
     */
    public ConfigPackage getPackage() {
        return pack;
    }

    /**
     * @return the ID of the conversation
     */
    public String getID() {
        return convID;
    }

    /**
     * @return the interceptor of the conversation
     */
    public Interceptor getInterceptor() {
        return interceptor;
    }

    /**
     * Starts the conversation, should be called asynchronously.
     *
     * @author Jakub Sapalski
     */
    private class Starter extends BukkitRunnable {

        private String[] options;

        public Starter(final String[] options) {
            super();
            this.options = options;
        }

        public void run() {
            // the conversation start event must be run on next tick
            final PlayerConversationStartEvent event = new PlayerConversationStartEvent(player, conv);
            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(event);
                }
            }.runTask(BetonQuest.getInstance());

            // stop the conversation if it's canceled
            if (event.isCancelled()) {
                return;
            }

            // now the conversation should start no matter what;
            // the inOut can be safely instantiated; doing it before
            // would leave it active while the conversation is not
            // started, causing it to display "null" all the time
            try {
                final String name = data.getConversationIO();
                final Class<? extends ConversationIO> convIO = plugin.getConvIO(name);
                conv.inOut = convIO.getConstructor(Conversation.class, String.class).newInstance(conv, playerID);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error when loading conversation IO");
                LogUtils.logThrowable(e);
                return;
            }

            // register listener for immunity and blocking commands
            Bukkit.getPluginManager().registerEvents(conv, BetonQuest.getInstance());

            // start interceptor if needed
            if (messagesDelaying) {
                try {
                    final String name = data.getInterceptor();
                    final Class<? extends Interceptor> interceptor = plugin.getInterceptor(name);
                    conv.interceptor = interceptor.getConstructor(Conversation.class, String.class).newInstance(conv, playerID);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error when loading interceptor");
                    LogUtils.logThrowable(e);
                    return;
                }
            }

            if (options == null) {
                options = data.getStartingOptions();

                // first select the option before sending message, so it
                // knows which is used
                selectOption(options, false);

                // check whether to add a prefix
                final String prefix = data.getPrefix(language, option);
                String prefixName = null;
                String[] prefixVariables = null;
                if (prefix != null) {
                    prefixName = "conversation_prefix";
                    prefixVariables = new String[]{prefix};
                }

                //only display status messages if conversationIO allows it
                if (conv.inOut.printMessages()) {
                    // print message about starting a conversation only if it
                    // is started, not resumed
                    conv.inOut.print(Config.parseMessage(playerID, "conversation_start", new String[]{data.getQuester(language)},
                            prefixName, prefixVariables));
                }
                //play the conversation start sound
                Config.playSound(playerID, "start");
            } else {
                // don't forget to select the option prior to printing its text
                selectOption(options, true);
            }

            // print NPC's text
            printNPCText();
            final ConversationOptionEvent optionEvent = new ConversationOptionEvent(player, conv, option, conv.option);

            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(optionEvent);
                }
            }.runTask(BetonQuest.getInstance());

        }
    }

    /**
     * Fires events from the option. Should be called in the main thread.
     *
     * @author Jakub Sapalski
     */
    private class NPCEventRunner extends BukkitRunnable {

        private String option;

        public NPCEventRunner(final String option) {
            super();
            this.option = option;
        }

        public void run() {
            // fire events
            for (final EventID event : data.getEventIDs(playerID, option, OptionType.NPC)) {
                BetonQuest.event(playerID, event);
            }
            new OptionPrinter(option).runTaskAsynchronously(BetonQuest.getInstance());
        }
    }

    /**
     * Fires events from the option. Should be called in the main thread.
     *
     * @author Jakub Sapalski
     */
    private class PlayerEventRunner extends BukkitRunnable {

        private String option;

        public PlayerEventRunner(final String option) {
            super();
            this.option = option;
        }

        public void run() {
            // fire events
            for (final EventID event : data.getEventIDs(playerID, option, OptionType.PLAYER)) {
                BetonQuest.event(playerID, event);
            }
            new ResponsePrinter(option).runTaskAsynchronously(BetonQuest.getInstance());
        }
    }

    /**
     * Prints the NPC response to the player. Should be called asynchronously.
     *
     * @author Jakub Sapalski
     */
    private class ResponsePrinter extends BukkitRunnable {

        private String option;

        public ResponsePrinter(final String option) {
            super();
            this.option = option;
        }

        public void run() {
            // don't forget to select the option prior to printing its text
            selectOption(data.getPointers(playerID, option, OptionType.PLAYER), false);
            // print to player npc's answer
            printNPCText();
            final ConversationOptionEvent event = new ConversationOptionEvent(player, conv, option, conv.option);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(event);
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    /**
     * Prints the options to the player. Should be called asynchronously.
     *
     * @author Jakub Sapalski
     */
    private class OptionPrinter extends BukkitRunnable {

        private String option;

        public OptionPrinter(final String option) {
            super();
            this.option = option;
        }

        public void run() {
            // print options
            printOptions(data.getPointers(playerID, option, OptionType.NPC));
        }
    }

    /**
     * Ends the conversation. Should be called in the main thread.
     *
     * @author Jakub Sapalski
     */
    private class ConversationEnder extends BukkitRunnable {
        private ConversationEnder() {
            super();
        }

        public void run() {
            endConversation();
        }
    }
}
