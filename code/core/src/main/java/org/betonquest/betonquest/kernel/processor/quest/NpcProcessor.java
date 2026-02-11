package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcConversation;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.service.ConditionManager;
import org.betonquest.betonquest.api.service.NpcManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationStarter;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Stores Npcs and starts Npc conversations.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class NpcProcessor extends TypedQuestProcessor<NpcIdentifier, NpcWrapper<?>> implements NpcManager {

    /**
     * The section in which the assignments from Npcs to conversations are stored.
     */
    private static final String NPC_SECTION = "npc_conversations";

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Plugin to load config.
     */
    private final BetonQuest plugin;

    /**
     * The {@link IdentifierFactory} to create {@link ConversationIdentifier}s.
     */
    private final IdentifierFactory<ConversationIdentifier> conversationIdentifierFactory;

    /**
     * Stores the last time the player interacted with an NPC.
     */
    private final Map<UUID, Long> npcInteractionLimiter = new HashMap<>();

    /**
     * Stores the conversations assigned to NPCs via the configuration.
     * The key could either be a Npcs name or its ID, depending on the configuration.
     */
    private final Map<NpcIdentifier, ConversationIdentifier> assignedConversations = new HashMap<>();

    /**
     * Hider for Npcs.
     */
    private final DefaultNpcHider npcHider;

    /**
     * The sender for busy notifications.
     */
    private final IngameNotificationSender busySender;

    /**
     * Starts conversations with Npcs.
     */
    private final ConversationStarter convStarter;

    /**
     * The minimum time between two interactions with an NPC.
     */
    private int interactionLimit;

    /**
     * If left-click interactions should also trigger conversation starts.
     */
    private boolean acceptNpcLeftClick;

    /**
     * Create a new Quest Npc Processor to store them.
     *
     * @param log                           the custom logger for this class
     * @param loggerFactory                 the logger factory used to create logger for the started conversations
     * @param npcIdentifierFactory          the identifier factory to create {@link NpcIdentifier}s for this type
     * @param conversationIdentifierFactory the identifier factory to create {@link ConversationIdentifier}s for this type
     * @param npcTypes                      the available npc types
     * @param pluginMessage                 the {@link PluginMessage} instance
     * @param plugin                        the plugin to load config
     * @param profileProvider               the profile provider instance
     * @param conditionManager              the condition manager
     * @param convStarter                   the starter for Npc conversations
     * @param instructionApi                the instruction api
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public NpcProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                        final IdentifierFactory<NpcIdentifier> npcIdentifierFactory,
                        final IdentifierFactory<ConversationIdentifier> conversationIdentifierFactory,
                        final NpcTypeRegistry npcTypes, final PluginMessage pluginMessage, final BetonQuest plugin,
                        final ProfileProvider profileProvider, final ConditionManager conditionManager, final ConversationStarter convStarter,
                        final InstructionApi instructionApi) {
        super(log, npcTypes, npcIdentifierFactory, instructionApi, "Npc", "npcs");
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.convStarter = convStarter;
        this.plugin = plugin;
        this.conversationIdentifierFactory = conversationIdentifierFactory;
        plugin.getServer().getPluginManager().registerEvents(new NpcListener(), plugin);
        this.npcHider = new DefaultNpcHider(loggerFactory.create(DefaultNpcHider.class), this,
                conditionManager, profileProvider, npcTypes, plugin.getQuestRegistries().identifier(), plugin.getInstructionApi());
        this.busySender = new IngameNotificationSender(log, pluginMessage, null, "NpcProcessor", NotificationLevel.ERROR, "busy");
    }

    @Override
    public void load(final QuestPackage pack) {
        super.load(pack);
        loadBindings(pack);
        npcHider.load(pack);
    }

    /**
     * Loads the npc references to start the conversation on interaction with them.
     *
     * @param pack the quest package to load the references
     */
    private void loadBindings(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(NPC_SECTION);
        if (section == null) {
            return;
        }
        final String packName = pack.getQuestPath();
        for (final String key : section.getKeys(false)) {
            if (key.contains(" ")) {
                log.warn(pack, NPC_SECTION + " name cannot contain spaces: '" + key + "' (in " + packName + " package)");
            } else if (!section.isString(key)) {
                log.warn(pack, NPC_SECTION + " value for key '" + key + "' (in " + packName + " package) is not a string");
            } else {
                try {
                    final NpcIdentifier npcID = getIdentifier(pack, key);
                    final ConversationIdentifier conversationID = conversationIdentifierFactory.parseIdentifier(pack, Objects.requireNonNull(section.getString(key)));
                    assignedConversations.put(npcID, conversationID);
                } catch (final QuestException exception) {
                    log.warn(pack, "Error while loading " + NPC_SECTION + " for key '" + key + "' (in " + packName + " package): " + exception.getMessage(), exception);
                }
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        ((NpcTypeRegistry) types).resetIdentifier();
        interactionLimit = plugin.getPluginConfig().getInt("npc.interaction_limit", 500);
        acceptNpcLeftClick = plugin.getPluginConfig().getBoolean("npc.accept_left_click");
        final int updateInterval = plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20);
        npcHider.reload(updateInterval, plugin);
    }

    @Override
    protected void postCreation(final NpcIdentifier identifier, final NpcWrapper<?> value) {
        ((NpcTypeRegistry) types).addIdentifier(identifier);
    }

    /**
     * The logic that determines if an NPC interaction starts a conversation.
     *
     * @param profile the player profile who clicked the NPC
     * @param npcIds  the ids to check for conversations
     * @param npc     the npc which was interacted with
     * @return if a conversation is started and the interact event should be cancelled
     */
    public boolean interactLogic(final Profile profile, final Set<NpcIdentifier> npcIds, final Npc<?> npc) {
        if (profile.getOnlineProfile().isEmpty()) {
            return false;
        }
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        if (!onlineProfile.getPlayer().hasPermission("betonquest.conversation")) {
            return false;
        }
        final UUID playerUUID = profile.getPlayerUUID();

        final Long lastClick = npcInteractionLimiter.get(playerUUID);
        final long currentClick = System.currentTimeMillis();
        if (lastClick != null && lastClick + interactionLimit >= currentClick) {
            return false;
        }
        npcInteractionLimiter.put(playerUUID, currentClick);

        return startConversation(onlineProfile, npcIds, npc, onlineProfile);
    }

    @SuppressWarnings("NullAway")
    private boolean startConversation(final OnlineProfile clicker, final Set<NpcIdentifier> identifier, final Npc<?> npc, final OnlineProfile onlineProfile) {
        ConversationIdentifier conversationID = null;
        NpcIdentifier selected = null;
        for (final NpcIdentifier npcID : identifier) {
            conversationID = assignedConversations.get(npcID);
            if (conversationID != null) {
                selected = npcID;
                break;
            }
        }

        if (conversationID == null) {
            log.debug("Profile '" + clicker.getProfileName() + "' clicked Npc '" + identifier
                    + "' but there is no conversation assigned to it.");
            return false;
        }

        if (CombatTagger.isTagged(onlineProfile)) {
            busySender.sendNotification(onlineProfile);
            return false;
        }

        log.debug("Profile '" + clicker.getProfileName() + "' clicked Npc '" + selected
                + "' and started conversation '" + conversationID + "'.");
        final Location center = npc.getLocation().orElseGet(() -> onlineProfile.getPlayer().getLocation());
        convStarter.startConversation(onlineProfile, conversationID, center, null,
                (onlineProfile1, id, center1, run)
                        -> new NpcConversation<>(loggerFactory.create(NpcConversation.class), pluginMessage, onlineProfile1, id, center1, run, npc));
        return true;
    }

    /**
     * Gets the NpcHider.
     *
     * @return the active npc hider
     */
    public DefaultNpcHider getNpcHider() {
        return npcHider;
    }

    @Override
    public Npc<?> get(@Nullable final Profile profile, final NpcIdentifier npcIdentifier) throws QuestException {
        return get(npcIdentifier).getNpc(profile);
    }

    /**
     * Listener for Conversation starting and Hiding with {@link Npc}s.
     */
    private class NpcListener implements Listener {

        /**
         * The default Constructor.
         */
        public NpcListener() {

        }

        /**
         * Attempts to start conversations on Npc interactions.
         *
         * @param event the interact event
         */
        @EventHandler(ignoreCancelled = true)
        public void onInteract(final NpcInteractEvent event) {
            if (event.getInteraction() == Interaction.LEFT && !acceptNpcLeftClick) {
                return;
            }
            if (interactLogic(event.getProfile(), event.getNpcIdentifier(), event.getNpc())) {
                event.setCancelled(true);
            }
        }

        /**
         * Applies the visibility on Player join.
         *
         * @param event the event to listen
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerJoin(final PlayerJoinEvent event) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    npcHider.applyVisibility(plugin.getProfileProvider().getProfile(event.getPlayer())));
        }

        /**
         * Applies the visibility on Extern change Player join.
         *
         * @param event the external change event to listen
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onExternalChange(final NpcVisibilityUpdateEvent event) {
            if (event.getNpc() == null) {
                npcHider.applyVisibility();
                return;
            }
            final Set<NpcIdentifier> identifier = ((NpcTypeRegistry) types).getIdentifier(event.getNpc(), null);
            for (final NpcIdentifier npcID : identifier) {
                npcHider.applyVisibility(npcID);
            }
        }
    }
}
