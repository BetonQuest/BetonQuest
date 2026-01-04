package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.ConversationOptionEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ConversationData.OptionType;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods", "NullAway"})
public class Conversation {

    /**
     * Common log message identifier separator.
     */
    private static final String FOR = "' for '";

    /**
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest plugin;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Callable that removes the conversation from the active ones.
     */
    private final Runnable endCallable;

    /**
     * The pack of this conversation.
     */
    private final QuestPackage pack;

    /**
     * Thread safety.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Profile taking this conversation.
     */
    private final OnlineProfile onlineProfile;

    /**
     * Player taking this conversation.
     */
    private final Player player;

    /**
     * The location at which the conversation was started. Used for checking if the player has moved too far away.
     * For an NPC based conversation this would be the location of the NPC.
     */
    private final Location center;

    /**
     * The ID of this conversation.
     */
    private final ConversationID identifier;

    /**
     * A map of options that the player can currently choose.
     * The key is the number of the option, the value is the option itself.
     * <br>
     * The conversationIO will pass an integer to the conversation, which will be used to get the option from this map.
     */
    private final Map<Integer, ResolvedOption> availablePlayerOptions = new HashMap<>();

    /**
     * Notification sender when conversation starts.
     */
    private final IngameNotificationSender startSender;

    /**
     * Notification sender when conversation ends.
     */
    private final IngameNotificationSender endSender;

    /**
     * The {@link ConversationIO} used to display this conversation.
     */
    private final ConversationIO inOut;

    /**
     * The {@link Interceptor} used to hide unrelated messages while the player is in this conversation.
     */
    private final Interceptor interceptor;

    /**
     * The current conversation state.
     */
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
     * Starts a new conversation between player and npc at given location.
     *
     * @param log            the logger that will be used for logging
     * @param pluginMessage  the {@link PluginMessage} instance
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param endCallable    the callable that removes the conversation from the active ones
     * @throws QuestException when required conversation objects could not be created
     */
    public Conversation(final BetonQuestLogger log, final PluginMessage pluginMessage, final OnlineProfile onlineProfile, final ConversationID conversationID,
                        final Location center, final Runnable endCallable) throws QuestException {
        this.log = log;
        this.endCallable = endCallable;
        this.plugin = BetonQuest.getInstance();
        this.onlineProfile = onlineProfile;
        this.player = onlineProfile.getPlayer();
        this.identifier = conversationID;
        this.pack = conversationID.getPackage();
        this.center = center;
        this.startSender = new IngameNotificationSender(log, pluginMessage, pack, conversationID.getFull(), NotificationLevel.INFO, "conversation_start");
        this.endSender = new IngameNotificationSender(log, pluginMessage, pack, conversationID.getFull(), NotificationLevel.INFO, "conversation_end");

        this.data = plugin.getFeatureApi().conversationApi().getData(conversationID);
        this.inOut = data.getPublicData().convIO().getValue(onlineProfile).parse(this, onlineProfile);
        this.interceptor = data.getPublicData().interceptor().getValue(onlineProfile).create(onlineProfile);
    }

    /**
     * Sets the data and option of this conversation to the one of the first option that the player meets the conditions for.
     *
     * @param options list of option pointers separated by commas
     * @param force   setting it to true will force the first option, even if
     *                conditions are not met
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private void selectOption(final List<ResolvedOption> options, final boolean force) {
        final List<ResolvedOption> inputOptions = force ? List.of(options.get(0)) : options;

        nextNPCOption = null;

        for (final ResolvedOption option : inputOptions) {
            // If we refer to another conversation starting options the name is null
            if (option.name() == null) {
                for (final String startingOptionName : option.conversationData().getStartingOptions()) {
                    if (force || plugin.getQuestTypeApi().conditions(onlineProfile, option.conversationData().getConditionIDs(startingOptionName, NPC))) {
                        this.data = option.conversationData();
                        this.nextNPCOption = new ResolvedOption(option.conversationData(), NPC, startingOptionName);
                        break;
                    }
                }
            } else {
                if (force || plugin.getQuestTypeApi().conditions(onlineProfile, option.conversationData().getConditionIDs(option.name(), NPC))) {
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
        if (nextNPCOption == null) {
            new ConversationEnder().runTask(plugin);
            return;
        }
        inOut.setNpcResponse(data.getPublicData().getQuester(log, onlineProfile), data.getText(onlineProfile, nextNPCOption));
        new NPCActionRunner(nextNPCOption).runTask(plugin);
    }

    /**
     * Passes given string as answer from player in a conversation.
     *
     * @param number the message player has sent on chat
     */
    public void passPlayerAnswer(final int number) {
        inOut.clear();
        final ResolvedOption playerOption = availablePlayerOptions.get(number);
        if (playerOption == null) {
            throw new IllegalStateException("No selectable player option found in conversation " + identifier);
        }
        new PlayerActionRunner(playerOption).runTask(plugin);
        availablePlayerOptions.clear();
    }

    /**
     * Selects all options the player can choose from based on the conditions.
     * Then passes these onto the conversationIO for printing.
     *
     * @param options list of pointers to player options separated by commas
     */
    private void printOptions(final List<ResolvedOption> options) {
        final List<Pair<ResolvedOption, CompletableFuture<Boolean>>> futuresOptions = new ArrayList<>();
        for (final ResolvedOption option : options) {
            final List<ConditionID> conditionIDs = option.conversationData().getConditionIDs(option.name(), option.type());
            final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                    () -> plugin.getQuestTypeApi().conditions(onlineProfile, conditionIDs));
            futuresOptions.add(Pair.of(option, future));
        }

        int optionsCount = 0;
        option:
        for (final Pair<ResolvedOption, CompletableFuture<Boolean>> future : futuresOptions) {
            try {
                if (!future.getValue().get(1, TimeUnit.SECONDS)) {
                    continue option;
                }
            } catch (final CancellationException | InterruptedException | ExecutionException | TimeoutException e) {
                log.reportException(pack, e);
                continue;
            }
            final ResolvedOption option = future.getKey();
            optionsCount++;
            availablePlayerOptions.put(optionsCount, option);

            try {
                inOut.addPlayerOption(data.getText(onlineProfile, option), data.getProperties(onlineProfile, option));
            } catch (final QuestException e) {
                log.warn(pack, "Error while adding option '" + option.name() + "': " + e.getMessage(), e);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                inOut.display();
            }
        }.runTask(plugin);
        // end conversations if there are no possible options
        if (availablePlayerOptions.isEmpty()) {
            new ConversationEnder().runTask(plugin);
        }
    }

    /**
     * Ends conversation, firing final actions and removing it from the list of
     * active conversations.
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

            log.debug(pack, "Ending conversation '" + identifier + FOR + onlineProfile + "'.");
            inOut.end(() -> {
                try {
                    plugin.getQuestTypeApi().actions(onlineProfile, data.getPublicData().finalActions().getValue(onlineProfile));
                } catch (final QuestException e) {
                    log.warn(pack, "Error while firing final actions: " + e.getMessage(), e);
                }

                endConversationDelayed();
            });
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void endConversationDelayed() {
        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (onlineProfile.getOnlineProfile().isPresent()) {
                        interceptor.end();
                        endSender.sendNotification(onlineProfile, new VariableReplacement("npc", data.getPublicData().getQuester(log, onlineProfile)));
                    }

                    endCallable.run();
                    new PlayerConversationEndEvent(onlineProfile, !plugin.getServer().isPrimaryThread(), Conversation.this).callEvent();
                }
            }.runTaskLaterAsynchronously(plugin, data.getPublicData().interceptorDelay().getValue(onlineProfile).longValue());
        } catch (final QuestException e) {
            log.warn(pack, "Error while ending conversation: " + e.getMessage(), e);
        }
    }

    /**
     * If the conversation is ended.
     *
     * @return whenever this conversation has already ended
     */
    public boolean isActive() {
        lock.readLock().lock();
        try {
            return state.isActive();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Send message to player, bypassing any message delaying if needed.
     *
     * @param message The message to send
     */
    public void sendMessage(final Component message) {
        if (state.isActive() && isActive()) {
            interceptor.sendMessage(message);
        } else {
            player.sendMessage(message);
        }
    }

    /**
     * Checks if the movement of the player should be blocked.
     *
     * @return true if the movement should be blocked, false otherwise
     */
    public boolean isMovementBlock() {
        try {
            return data.getPublicData().blockMovement().getValue(onlineProfile);
        } catch (final QuestException e) {
            log.warn(pack, "Error resolving if movement should be blocked: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if the conversation has a next NPC option.
     *
     * @return true if there is a next NPC option, false otherwise
     */
    public boolean hasNextNPCOption() {
        return nextNPCOption != null;
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
            inOut.end(() -> {
            });

            final PlayerConversationState state = new PlayerConversationState(identifier, nextNPCOption.name(), center);
            plugin.getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION, state.toString(), onlineProfile.getProfileUUID().toString()));

            interceptor.end();

            endCallable.run();
            new PlayerConversationEndEvent(onlineProfile, !plugin.getServer().isPrimaryThread(), this).callEvent();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the conversation center.
     *
     * @return the location where the conversation has been started
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Gets the used conversation io.
     *
     * @return the ConversationIO object used by this conversation
     */
    public ConversationIO getIO() {
        return inOut;
    }

    /**
     * Gets the current conversation data. It may change when using cross conversation pointers.
     *
     * @return the data of the conversation
     */
    public ConversationData getData() {
        return data;
    }

    /**
     * Gets the conversation source package.
     *
     * @return the package containing this conversation
     */
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * Gets the conversation id.
     *
     * @return the ID of the conversation
     */
    public ConversationID getID() {
        return identifier;
    }

    private List<ResolvedOption> resolvePointers(final ResolvedOption option) throws QuestException {
        final ConversationData nextConvData = option.conversationData();
        final List<String> rawPointers = nextConvData.getPointers(onlineProfile, option);
        final List<ResolvedOption> pointers = new ArrayList<>();
        for (final String pointer : rawPointers) {
            final OptionType nextType = option.type() == PLAYER ? NPC : PLAYER;
            pointers.add(nextConvData.resolveOption(new ConversationOptionID(plugin.getQuestPackageManager(), nextConvData.getPack(), pointer), nextType));
        }
        return pointers;
    }

    /**
     * Starts the conversation, has to be called asynchronously.
     */
    public static class Starter {

        /**
         * The option separator.
         */
        private static final String OPTIONS_SEPARATOR = ".";

        /**
         * A list of options the conversation will start from.
         */
        private final List<String> startingOptions;

        /**
         * If the condition check for starting options should be bypassed.
         */
        private final boolean force;

        /**
         * Conversation to start.
         */
        private final Conversation conversation;

        /**
         * Starts a conversation at the given option.
         *
         * @param conversation   the conversation to start
         * @param startingOption the name of the option to start at
         */
        public Starter(final Conversation conversation, final String startingOption) {
            super();
            this.conversation = conversation;
            String firstOption = startingOption;
            if (!startingOption.contains(OPTIONS_SEPARATOR)) {
                firstOption = conversation.getID().get() + OPTIONS_SEPARATOR + startingOption;
            }
            this.startingOptions = List.of(firstOption);
            this.force = true;
        }

        /**
         * Starts a conversation with the first available starting option.
         *
         * @param conversation the conversation to start
         */
        public Starter(final Conversation conversation) {
            super();
            this.conversation = conversation;
            this.startingOptions = conversation.data.getStartingOptions();
            this.force = false;
        }

        /**
         * Starts the conversation, if not already started.
         *
         * @throws QuestException when starting options could not be resolved
         */
        public void start() throws QuestException {
            if (conversation.state.isStarted()) {
                return;
            }

            conversation.lock.writeLock().lock();
            try {
                if (conversation.state.isStarted()) {
                    return;
                }

                final OnlineProfile onlineProfile = conversation.onlineProfile;

                conversation.startSender.sendNotification(onlineProfile,
                        new VariableReplacement("npc", conversation.data.getPublicData().getQuester(conversation.log, onlineProfile)));
                conversation.state = ConversationState.ACTIVE;

                conversation.inOut.begin();
                conversation.interceptor.begin();

                conversation.selectOption(resolveOptions(startingOptions), force);
                conversation.printNPCText();
                new ConversationOptionEvent(onlineProfile, conversation, conversation.nextNPCOption, conversation.nextNPCOption).callEvent();
            } finally {
                conversation.lock.writeLock().unlock();
            }
        }

        private List<ResolvedOption> resolveOptions(final List<String> startingOptions) throws QuestException {
            final List<ResolvedOption> resolvedOptions = new ArrayList<>();
            for (final String startingOption : startingOptions) {
                final ResolvedOption resolvedOption;
                resolvedOption = conversation.data.resolveOption(new ConversationOptionID(conversation.plugin.getQuestPackageManager(),
                        conversation.pack, startingOption), NPC);
                resolvedOptions.add(resolvedOption);
            }
            return resolvedOptions;
        }
    }

    /**
     * Fires actions from an NPC option. Should be called on the main thread.
     */
    private final class NPCActionRunner extends BukkitRunnable {

        /**
         * The NPC option that has been selected and should be printed.
         */
        private final ResolvedOption npcOption;

        private NPCActionRunner(final ResolvedOption npcOption) {
            super();
            this.npcOption = npcOption;
        }

        @Override
        public void run() {
            plugin.getQuestTypeApi().actions(onlineProfile, data.getActionIDs(onlineProfile, npcOption, NPC));
            new OptionPrinter(npcOption).runTaskAsynchronously(plugin);
        }
    }

    /**
     * Fires actions from the option. Should be called in the main thread.
     */
    private final class PlayerActionRunner extends BukkitRunnable {

        /**
         * The option that has been selected by the player.
         */
        private final ResolvedOption playerOption;

        private PlayerActionRunner(final ResolvedOption playerOption) {
            super();
            this.playerOption = playerOption;
        }

        @Override
        public void run() {
            plugin.getQuestTypeApi().actions(onlineProfile, data.getActionIDs(onlineProfile, playerOption, PLAYER));
            new ResponsePrinter(playerOption).runTaskAsynchronously(plugin);
        }
    }

    /**
     * Prints the NPC response to the player. Should be called asynchronously.
     */
    private final class ResponsePrinter extends BukkitRunnable {

        /**
         * The option that has been selected by the player and should be printed.
         */
        private final ResolvedOption playerOption;

        private ResponsePrinter(final ResolvedOption playerOption) {
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

                new ConversationOptionEvent(onlineProfile, Conversation.this, playerOption,
                        Conversation.this.nextNPCOption).callEvent();
            } catch (final QuestException e) {
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
    private final class OptionPrinter extends BukkitRunnable {

        /**
         * The option that has been selected and should be printed.
         */
        private final ResolvedOption npcOption;

        private OptionPrinter(final ResolvedOption npcOption) {
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
            } catch (final QuestException e) {
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
    private final class ConversationEnder extends BukkitRunnable {

        private ConversationEnder() {
            super();
        }

        @Override
        public void run() {
            endConversation();
        }
    }
}
