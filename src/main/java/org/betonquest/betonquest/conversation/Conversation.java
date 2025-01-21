package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.ConversationOptionEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationStartEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.ConversationData.OptionType;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.quest.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.betonquest.betonquest.conversation.ConversationData.OptionType.NPC;
import static org.betonquest.betonquest.conversation.ConversationData.OptionType.PLAYER;

/**
 * Manages an active conversation between a player and a NPC.
 * Handles the conversation flow based on {@link ConversationData}.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyFields", "PMD.TooManyMethods", "PMD.CommentRequired",
        "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.AvoidDuplicateLiterals",
        "PMD.CouplingBetweenObjects", "NullAway"})
public class Conversation implements Listener {

    /**
     * The map of all active conversations.
     */
    private static final Map<Profile, Conversation> ACTIVE_CONVERSATIONS = new ConcurrentHashMap<>();

    /**
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest plugin;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final QuestPackage pack;

    /**
     * Thread safety
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final OnlineProfile onlineProfile;

    private final Player player;

    private final String language;

    /**
     * The location at which the conversation was started. Used for checking if the player has moved too far away.
     * For an NPC based conversation this would be the location of the NPC.
     */
    private final Location center;

    /**
     * The ID of this conversation.
     */
    private final ConversationID identifier;

    private final List<String> blacklist;

    private final Conversation conv;

    /**
     * A map of options that the player can currently choose.
     * The key is the number of the option, the value is the option itself.
     * <br>
     * The conversationIO will pass an integer to the conversation, which will be used to get the option from this map.
     */
    private final Map<Integer, ResolvedOption> availablePlayerOptions = new HashMap<>();

    private final boolean messagesDelaying;

    @SuppressWarnings("PMD.AvoidUsingVolatile")
    protected volatile ConversationState state = ConversationState.CREATED;

    /**
     * The next NPC option that will be printed. Set by {@link #selectOption(List, boolean)}.
     */
    @Nullable
    protected ResolvedOption nextNPCOption;

    /**
     * The conversation data that is currently being used.
     */
    private ConversationData data;

    /**
     * The {@link ConversationIO} used to display this conversation.
     */
    @Nullable
    private ConversationIO inOut;

    /**
     * The {@link Interceptor} used to hide unrelated messages while the player is in this conversation.
     */
    @Nullable
    private Interceptor interceptor;

    /**
     * Starts a new conversation between player and npc at given location. It uses
     * starting options to determine where to start.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     */
    public Conversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID, final Location center) {
        this(log, onlineProfile, conversationID, center, null);
    }

    /**
     * Starts a new conversation between player and npc at given location,
     * starting with the given option. If the option is null, then it will start
     * from the beginning.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param startingOption name of the option which the conversation should start at
     */
    public Conversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                        final Location center, @Nullable final String startingOption) {
        this.log = log;
        this.conv = this;
        this.plugin = BetonQuest.getInstance();
        this.onlineProfile = onlineProfile;
        this.player = onlineProfile.getPlayer();
        this.identifier = conversationID;
        this.pack = conversationID.getPackage();
        this.language = plugin.getPlayerDataStorage().get(onlineProfile).getLanguage();
        this.center = center;
        this.blacklist = plugin.getPluginConfig().getStringList("cmd_blacklist");
        this.messagesDelaying = Boolean.parseBoolean(plugin.getPluginConfig().getString("display_chat_after_conversation"));

        final ConversationData tmpData = plugin.getConversation(conversationID);
        if (tmpData == null) {
            log.error(pack, "Tried to start conversation '" + conversationID.getFullID() + "' but it is not loaded! Check for errors on /bq reload!");
            return;
        }
        this.data = tmpData;
        if (ACTIVE_CONVERSATIONS.containsKey(onlineProfile)) {
            log.debug(pack, onlineProfile + " is in conversation right now, returning.");
            return;
        }

        ACTIVE_CONVERSATIONS.put(onlineProfile, conv);

        if (startingOption == null) {
            log.debug(pack, "Starting conversation '" + conversationID.getFullID() + "' for '" + onlineProfile + "'.");
            new Starter().runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            String firstOption = startingOption;
            if (!startingOption.contains(".")) {
                firstOption = conversationID.getBaseID() + "." + startingOption;
            }
            log.debug(pack, "Starting conversation '" + conversationID.getFullID() + "' for '" + onlineProfile + "'.");
            new Starter(firstOption).runTaskAsynchronously(BetonQuest.getInstance());
        }
    }

    /**
     * Checks if the player is in a conversation.
     *
     * @param profile the {@link Profile} of the player
     * @return if the player is the list of active conversations
     */
    public static boolean containsPlayer(final Profile profile) {
        return ACTIVE_CONVERSATIONS.containsKey(profile);
    }

    /**
     * Gets this player's active conversation.
     *
     * @param profile the {@link Profile} of the player
     * @return player's active conversation or null if there is no conversation
     */
    @Nullable
    public static Conversation getConversation(final Profile profile) {
        return ACTIVE_CONVERSATIONS.get(profile);
    }

    /**
     * Sets the data and option of this conversation to the one of the first option that the player meets the conditions for.
     *
     * @param options list of option pointers separated by commas
     * @param force   setting it to true will force the first option, even if
     *                conditions are not met
     */
    private void selectOption(final List<ResolvedOption> options, final boolean force) {
        final List<ResolvedOption> inputOptions = force ? List.of(options.get(0)) : options;

        nextNPCOption = null;

        for (final ResolvedOption option : inputOptions) {
            // If we refer to another conversation starting options the name is null
            if (option.name() == null) {
                for (final String startingOptionName : option.conversationData().getStartingOptions()) {
                    if (force || BetonQuest.conditions(onlineProfile, option.conversationData().getConditionIDs(startingOptionName, NPC))) {
                        this.data = option.conversationData();
                        this.nextNPCOption = new ResolvedOption(option.conversationData(), NPC, startingOptionName);
                        break;
                    }
                }
            } else {
                if (force || BetonQuest.conditions(onlineProfile, option.conversationData().getConditionIDs(option.name(), NPC))) {
                    this.data = option.conversationData();
                    this.nextNPCOption = option;
                    break;
                }
            }
        }
    }

    /**
     * Sends to the player the text said by NPC. It uses the selected option and
     * displays it.
     * <br>
     * Note: this method now requires a prior call to
     * selectOption()
     */
    private void printNPCText() {
        // if there are no possible options, end conversation
        if (nextNPCOption == null) {
            new ConversationEnder().runTask(BetonQuest.getInstance());
            return;
        }

        String text = data.getText(onlineProfile, language, nextNPCOption);
        text = ChatColor.translateAlternateColorCodes('&', text);

        // print option to the player
        inOut.setNpcResponse(data.getQuester(language), text);

        new NPCEventRunner(nextNPCOption).runTask(BetonQuest.getInstance());
    }

    /**
     * Passes given string as answer from player in a conversation.
     *
     * @param number the message player has sent on chat
     */
    public void passPlayerAnswer(final int number) {

        inOut.clear();

        new PlayerEventRunner(availablePlayerOptions.get(number)).runTask(BetonQuest.getInstance());

        // clear hashmap
        availablePlayerOptions.clear();
    }

    /**
     * Selects all options the player can choose from based on the conditions.
     * Then passes these onto the conversationIO for printing.
     *
     * @param options list of pointers to player options separated by commas
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private void printOptions(final List<ResolvedOption> options) {
        final List<Pair<ResolvedOption, List<CompletableFuture<Boolean>>>> futuresOptions = new ArrayList<>();
        for (final ResolvedOption option : options) {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID conditionID : option.conversationData().getConditionIDs(option.name(), option.type())) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> BetonQuest.condition(onlineProfile, conditionID));
                conditions.add(future);
            }
            futuresOptions.add(Pair.of(option, conditions));
        }

        int optionsCount = 0;
        option:
        for (final Pair<ResolvedOption, List<CompletableFuture<Boolean>>> future : futuresOptions) {
            try {
                for (final CompletableFuture<Boolean> completableFuture : future.getValue()) {
                    if (!completableFuture.get(1, TimeUnit.SECONDS)) {
                        continue option;
                    }
                }
            } catch (final CancellationException | InterruptedException | ExecutionException | TimeoutException e) {
                log.reportException(pack, e);
                continue;
            }
            final ResolvedOption option = future.getKey();
            optionsCount++;
            availablePlayerOptions.put(optionsCount, option);

            // replace variables with their values
            String text = data.getText(onlineProfile, language, option);
            text = ChatColor.translateAlternateColorCodes('&', text);

            inOut.addPlayerOption(text);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                inOut.display();
            }
        }.runTask(BetonQuest.getInstance());
        // end conversations if there are no possible options
        if (availablePlayerOptions.isEmpty()) {
            new ConversationEnder().runTask(BetonQuest.getInstance());
        }
    }

    /**
     * Ends conversation, firing final events and removing it from the list of
     * active conversations
     */
    public void endConversation() {
        if (state.isInactive()) {
            return;
        }
        if (plugin.getServer().isPrimaryThread()) {
            if (!lock.writeLock().tryLock()) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::endConversation);
                return;
            }
        } else {
            lock.writeLock().lock();
        }
        try {
            if (state.isInactive()) {
                return;
            }
            state = ConversationState.ENDED;

            log.debug(pack, "Ending conversation '" + conv.getID().getFullID() + "' for '" + onlineProfile + "'.");
            inOut.end();
            // fire final events
            for (final EventID event : data.getFinalEvents()) {
                BetonQuest.event(onlineProfile, event);
            }
            //only display status messages if conversationIO allows it
            if (conv.inOut.printMessages()) {
                // print message
                conv.inOut.print(Config.parseMessage(pack, onlineProfile, "conversation_end", data.getQuester(language)));
            }
            //play conversation end sound
            Config.playSound(onlineProfile, "end");

            // End interceptor after a second
            if (interceptor != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        interceptor.end();
                    }
                }.runTaskLaterAsynchronously(BetonQuest.getInstance(), 20);
            }

            // delete conversation
            ACTIVE_CONVERSATIONS.remove(onlineProfile);
            HandlerList.unregisterAll(this);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(onlineProfile, Conversation.this));
                }
            }.runTask(BetonQuest.getInstance());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return whenever this conversation has already ended
     */
    public boolean isEnded() {
        lock.readLock().lock();
        try {
            return state.isEnded();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Send message to player, bypassing any message delaying if needed
     *
     * @param message The message to send
     */
    public void sendMessage(final String message) {
        if (interceptor == null) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(message));
        } else {
            interceptor.sendMessage(message);
        }
    }

    public void sendMessage(final BaseComponent... message) {
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
        final String cmdName = event.getMessage().split(" ")[0].substring(1);
        if (blacklist.contains(cmdName)) {
            event.setCancelled(true);
            try {
                Config.sendNotify(getPackage(), PlayerConverter.getID(event.getPlayer()), "command_blocked", "command_blocked,error");
            } catch (final QuestException e) {
                log.warn(pack, "The notify system was unable to play a sound for the 'command_blocked' category. Error was: '" + e.getMessage() + "'", e);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        // prevent damage to (or from) player while in conversation
        if (event.getEntity() instanceof Player && PlayerConverter.getID((Player) event.getEntity()).equals(onlineProfile)
                || event.getDamager() instanceof Player
                && PlayerConverter.getID((Player) event.getDamager()).equals(onlineProfile)) {
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
        if (state.isInactive()) {
            return;
        }

        lock.readLock().lock();
        try {
            if (state.isInactive()) {
                return;
            }
            if (inOut == null) {
                log.warn(pack, "Conversation IO is not loaded, conversation will end for player "
                        + onlineProfile.getProfileName());
                ACTIVE_CONVERSATIONS.remove(onlineProfile);
                HandlerList.unregisterAll(this);
                return;
            }
            inOut.end();

            // save the conversation to the database
            final PlayerConversationState state = new PlayerConversationState(identifier, nextNPCOption.name(), center);
            plugin.getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION, state.toString(), onlineProfile.getProfileUUID().toString()));

            // End interceptor
            if (interceptor != null) {
                interceptor.end();
            }

            // delete conversation
            ACTIVE_CONVERSATIONS.remove(onlineProfile);
            HandlerList.unregisterAll(this);

            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(onlineProfile, Conversation.this));
                }
            }.runTask(BetonQuest.getInstance());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return the location where the conversation has been started
     */
    public Location getCenter() {
        return center;
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
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * @return the ID of the conversation
     */
    public ConversationID getID() {
        return identifier;
    }

    /**
     * @return the interceptor of the conversation
     */
    @Nullable
    public Interceptor getInterceptor() {
        return interceptor;
    }

    private List<ResolvedOption> resolvePointers(final ResolvedOption option) throws ObjectNotFoundException, QuestException {
        final ConversationData nextConvData = option.conversationData();
        final List<String> rawPointers = nextConvData.getPointers(onlineProfile, option);
        final List<ResolvedOption> pointers = new ArrayList<>();
        for (final String pointer : rawPointers) {
            final OptionType nextType = option.type() == PLAYER ? NPC : PLAYER;
            pointers.add(new ConversationOptionResolver(plugin, nextConvData.getPack(), nextConvData.getName(), nextType, pointer).resolve());
        }
        return pointers;
    }

    /**
     * Starts the conversation, should be called asynchronously.
     */
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private class Starter extends BukkitRunnable {

        /**
         * A list of options the conversation will start from.
         */
        private List<String> startingOptions = new ArrayList<>();

        /**
         * Starts a conversation at the given option.
         *
         * @param startingOption the name of the option to start at
         */
        public Starter(final String startingOption) {
            super();
            startingOptions.add(startingOption);
        }

        /**
         * Starts a conversation with the first available starting option.
         */
        public Starter() {
            super();
        }

        @SuppressWarnings("PMD.NcssCount")
        @Override
        public void run() {
            if (state.isStarted()) {
                return;
            }

            lock.writeLock().lock();
            try {
                if (state.isStarted()) {
                    return;
                }
                state = ConversationState.ACTIVE;
                // the conversation start event must be run on next tick
                final PlayerConversationStartEvent event = new PlayerConversationStartEvent(onlineProfile, conv);
                final Future<Void> eventDispatcherTask = Bukkit.getServer().getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> {
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    return null;
                });

                try {
                    eventDispatcherTask.get(1, TimeUnit.SECONDS);
                } catch (InterruptedException | TimeoutException exception) {
                    log.warn(pack, "Calling PlayerConversationStartEvent took too long.", exception);
                } catch (final ExecutionException exception) {
                    log.error(pack, "Error while calling PlayerConversationStartEvent.", exception);
                    ACTIVE_CONVERSATIONS.remove(onlineProfile);
                    return;
                }

                // stop the conversation if it's canceled
                if (event.isCancelled()) {
                    log.debug(pack, "Conversation '" + conv.getID().getFullID() + "' for '" + player.getPlayerProfile() + "' has been "
                            + "canceled because it's PlayerConversationStartEvent has been canceled.");
                    ACTIVE_CONVERSATIONS.remove(onlineProfile);
                    return;
                }

                // now the conversation should start no matter what;
                // the inOut can be safely instantiated; doing it before
                // would leave it active while the conversation is not
                // started, causing it to display "null" all the time
                try {
                    final String name = data.getConversationIO();
                    final ConversationIORegistry.ConversationIOFactory factory = Utils.getNN(
                            plugin.getFeatureRegistries().conversationIO().getFactory(name),
                            "No '" + name + "' registered!");
                    conv.inOut = factory.parse(conv, onlineProfile);
                } catch (final QuestException e) {
                    log.warn(pack, "Error when loading conversation IO: " + e.getMessage(), e);
                    return;
                }

                // register listener for immunity and blocking commands
                Bukkit.getPluginManager().registerEvents(conv, BetonQuest.getInstance());

                // start interceptor if needed
                if (messagesDelaying) {
                    try {
                        final String name = data.getInterceptor();
                        final InterceptorRegistry.InterceptorFactory factory = Utils.getNN(
                                plugin.getFeatureRegistries().interceptor().getFactory(name),
                                "No '" + name + "' registered!");
                        conv.interceptor = factory.parse(conv, onlineProfile);
                    } catch (final QuestException e) {
                        log.warn(pack, "Error when loading interceptor: " + e.getMessage(), e);
                        return;
                    }
                }

                if (startingOptions.isEmpty()) {
                    startingOptions = data.getStartingOptions();
                    final List<ResolvedOption> resolvedOptions = resolveOptions(startingOptions);
                    // first select the option before sending message, so it
                    // knows which is used
                    selectOption(resolvedOptions, false);

                    // check whether to add a prefix
                    final String prefix = data.getPrefix(language, nextNPCOption);
                    String prefixName = null;
                    String[] prefixVariables = null;
                    if (prefix != null) {
                        prefixName = "conversation_prefix";
                        prefixVariables = new String[]{prefix};
                    }

                    //only display status messages if conversationIO allows it
                    if (conv.inOut.printMessages()) {
                        // print message about starting a conversation only if it is started, not resumed
                        conv.inOut.print(Config.parseMessage(pack, onlineProfile, "conversation_start", new String[]{data.getQuester(language)},
                                prefixName, prefixVariables));
                    }

                    Config.playSound(onlineProfile, "start");
                } else {
                    final List<ResolvedOption> resolvedOptions = resolveOptions(startingOptions);
                    selectOption(resolvedOptions, true);
                }

                printNPCText();
                final ConversationOptionEvent optionEvent = new ConversationOptionEvent(PlayerConverter.getID(player), conv, nextNPCOption, conv.nextNPCOption);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(optionEvent);
                    }
                }.runTask(BetonQuest.getInstance());
            } finally {
                lock.writeLock().unlock();
            }
        }

        @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
        private List<ResolvedOption> resolveOptions(final List<String> startingOptions) {
            final List<ResolvedOption> resolvedOptions = new ArrayList<>();
            for (final String startingOption : startingOptions) {
                final ResolvedOption resolvedOption;
                try {
                    resolvedOption = new ConversationOptionResolver(plugin, pack, identifier.getBaseID(), NPC, startingOption).resolve();
                } catch (final QuestException | ObjectNotFoundException e) {
                    log.reportException(pack, e);
                    throw new IllegalStateException("Cannot continue starting conversation without options.", e);
                }
                resolvedOptions.add(resolvedOption);
            }
            return resolvedOptions;
        }
    }

    /**
     * Fires events from an NPC option. Should be called on the main thread.
     */
    private class NPCEventRunner extends BukkitRunnable {

        /**
         * The NPC option that has been selected and should be printed.
         */
        private final ResolvedOption npcOption;

        public NPCEventRunner(final ResolvedOption npcOption) {
            super();
            this.npcOption = npcOption;
        }

        @Override
        public void run() {
            for (final EventID event : data.getEventIDs(onlineProfile, npcOption, NPC)) {
                BetonQuest.event(onlineProfile, event);
            }
            new OptionPrinter(npcOption).runTaskAsynchronously(BetonQuest.getInstance());
        }
    }

    /**
     * Fires events from the option. Should be called in the main thread.
     */
    private class PlayerEventRunner extends BukkitRunnable {

        /**
         * The option that has been selected by the player.
         */
        private final ResolvedOption playerOption;

        /**
         * Creates a new PlayerEventRunner with the option that has been selected by the player.
         *
         * @param playerOption the option that has been selected by the player
         */
        public PlayerEventRunner(final ResolvedOption playerOption) {
            super();
            this.playerOption = playerOption;
        }

        @Override
        public void run() {
            for (final EventID event : data.getEventIDs(onlineProfile, playerOption, PLAYER)) {
                BetonQuest.event(onlineProfile, event);
            }
            new ResponsePrinter(playerOption).runTaskAsynchronously(BetonQuest.getInstance());
        }
    }

    /**
     * Prints the NPC response to the player. Should be called asynchronously.
     */
    private class ResponsePrinter extends BukkitRunnable {

        /**
         * The option that has been selected by the player and should be printed.
         */
        private final ResolvedOption playerOption;

        public ResponsePrinter(final ResolvedOption playerOption) {
            super();
            this.playerOption = playerOption;
        }

        @Override
        public void run() {
            if (state.isInactive()) {
                return;
            }
            lock.readLock().lock();
            try {
                if (state.isInactive()) {
                    return;
                }

                selectOption(resolvePointers(playerOption), false);
                printNPCText();

                final ConversationOptionEvent event = new ConversationOptionEvent(PlayerConverter.getID(player), conv, playerOption, conv.nextNPCOption);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }
                }.runTask(BetonQuest.getInstance());
            } catch (final QuestException | ObjectNotFoundException e) {
                log.reportException(pack, e);
                throw new IllegalStateException("Cannot ensure a valid conversation flow with unresolvable pointers.", e);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * Prints possible player options to a NPC option to the player. Should be called asynchronously.
     */
    private class OptionPrinter extends BukkitRunnable {

        /**
         * The option that has been selected and should be printed.
         */
        private final ResolvedOption npcOption;

        public OptionPrinter(final ResolvedOption npcOption) {
            super();
            this.npcOption = npcOption;
        }

        @Override
        public void run() {
            if (state.isInactive()) {
                return;
            }
            lock.readLock().lock();
            try {
                if (state.isInactive()) {
                    return;
                }
                printOptions(resolvePointers(npcOption));
            } catch (final QuestException | ObjectNotFoundException e) {
                log.reportException(pack, e);
                throw new IllegalStateException("Cannot ensure a valid conversation flow with unresolvable options.", e);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * Ends the conversation. Should be called in the main thread.
     */
    private class ConversationEnder extends BukkitRunnable {
        public ConversationEnder() {
            super();
        }

        @Override
        public void run() {
            endConversation();
        }
    }
}
