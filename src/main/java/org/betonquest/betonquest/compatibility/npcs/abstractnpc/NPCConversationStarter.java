package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Starts new conversations with Citizen NPCs.
 */
public abstract class NPCConversationStarter implements Listener {
    /**
     * The section in which the assignments from NPCs to conversations are stored.
     */
    private static final String NPC_SECTION = "npcs";

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Stores the last time the player interacted with an NPC.
     */
    private final Map<UUID, Long> npcInteractionLimiter = new HashMap<>();

    /**
     * Stores the conversations assigned to NPCs via the configuration.
     * The key could either be an NPC's name or its ID, depending on the configuration.
     */
    private final Map<String, ConversationID> assignedConversations = new HashMap<>();

    /**
     * A listener for right-clicking a Citizens NPC.
     */
    @Nullable
    private Listener rightClick;

    /**
     * A listener for left-clicking a Citizens NPC.
     */
    @Nullable
    private Listener leftClick;

    /**
     * The minimum time between two interactions with an NPC.
     */
    private int interactionLimit;

    /**
     * Initializes the listener.
     *
     * @param loggerFactory the logger factory used to create logger for the started conversations
     * @param log           the custom logger instance for this class
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public NPCConversationStarter(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        reload();
    }

    /**
     * Reloads the listeners.
     */
    public final void reload() {
        if (rightClick != null) {
            HandlerList.unregisterAll(rightClick);
        }
        if (leftClick != null) {
            HandlerList.unregisterAll(leftClick);
        }

        assignedConversations.clear();
        loadNPCConversationAssignments();

        final BetonQuest plugin = BetonQuest.getInstance();

        rightClick = newRightClickListener();
        Bukkit.getPluginManager().registerEvents(rightClick, plugin);

        if (plugin.getPluginConfig().getBoolean("acceptNPCLeftClick")) {
            leftClick = newLeftClickListener();
            Bukkit.getPluginManager().registerEvents(leftClick, plugin);
        }
        interactionLimit = plugin.getPluginConfig().getInt("npcInteractionLimit", 500);
    }

    /**
     * Gets a listener to get left-clicks on a NPC.
     *
     * @return a new left click listener
     */
    protected abstract Listener newLeftClickListener();

    /**
     * Gets a listener to get right-clicks on a NPC.
     *
     * @return a new right click listener
     */
    protected abstract Listener newRightClickListener();

    /**
     * The logic that determines if an NPC interaction starts a conversation.
     *
     * @param clicker     the player who clicked the NPC
     * @param npcSupplier the supplier for lazy instantiation when the NPC is needed
     * @return if a conversation is started and the interact event should be cancelled
     */
    protected boolean interactLogic(final Player clicker, final Supplier<BQNPCAdapter> npcSupplier) {
        if (!clicker.hasPermission("betonquest.conversation")) {
            return false;
        }
        final UUID playerUUID = clicker.getUniqueId();

        final Long lastClick = npcInteractionLimiter.get(playerUUID);
        final long currentClick = new Date().getTime();
        if (lastClick != null && lastClick + interactionLimit >= currentClick) {
            return false;
        }
        npcInteractionLimiter.put(playerUUID, currentClick);

        final OnlineProfile onlineProfile = PlayerConverter.getID(clicker);
        if (CombatTagger.isTagged(onlineProfile)) {
            try {
                Config.sendNotify(null, onlineProfile, "busy", "busy,error");
            } catch (final QuestRuntimeException e) {
                log.warn("The notify system was unable to play a sound for the 'busy' category. Error was: '" + e.getMessage() + "'", e);
            }
            return false;
        }

        return startConversation(clicker, npcSupplier, onlineProfile);
    }

    private boolean startConversation(final Player clicker, final Supplier<BQNPCAdapter> npcSupplier, final OnlineProfile onlineProfile) {
        final BQNPCAdapter npc = npcSupplier.get();
        final String npcId = npc.getId();
        final String npcName = npc.getName();

        final boolean npcsByName = "true".equalsIgnoreCase(Config.getConfigString("citizens_npcs_by_name"));

        final String selector = npcsByName ? npcName : npcId;
        final ConversationID conversationID = assignedConversations.get(selector);

        if (conversationID == null) {
            log.debug("Player '" + clicker.getName() + "' clicked NPC '" + npcId + "' but there is no conversation assigned to it.");
            return false;
        } else {
            startConversation(onlineProfile, conversationID, npc);
            return true;
        }
    }

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID the ID of the conversation
     * @param npc            the npc adapter representing the NPC used to start
     */
    protected abstract void startConversation(OnlineProfile onlineProfile, ConversationID conversationID, BQNPCAdapter npc);

    private void loadNPCConversationAssignments() {
        for (final Entry<String, QuestPackage> entry : Config.getPackages().entrySet()) {
            final QuestPackage pack = entry.getValue();

            final ConfigurationSection assignments = pack.getConfig().getConfigurationSection(NPC_SECTION);

            if (assignments == null) {
                continue;
            }
            for (final Entry<String, Object> assignment : assignments.getValues(false).entrySet()) {
                final ConversationID conversationID;
                final String npcID = assignment.getKey();
                final String conversationIDPath = assignment.getValue().toString();
                try {
                    conversationID = new ConversationID(pack, conversationIDPath);
                    assignedConversations.put(npcID, conversationID);
                } catch (final ObjectNotFoundException e) {
                    log.warn("Conversation '" + conversationIDPath + "' assigned to NPC '" + npcID + "' in package '" + pack.getQuestPath() + "' does not exist.", e);
                }
            }
        }
    }
}
