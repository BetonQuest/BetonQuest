package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.objective.NPCKillObjectiveFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.CitizensHider;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Integrator for Citizens.
 */
public class CitizensIntegrator implements Integrator {

    /**
     * Handles NPC movement of the {@link CitizensMoveEvent}.
     */
    @SuppressWarnings("NullAway.Init")
    private static CitizensMoveController citizensMoveController;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The compatibility instance to use for checking other hooks.
     */
    private final Compatibility compatibility;

    /**
     * The default Constructor.
     *
     * @param compatibility the compatibility instance to use for checking other hooks
     */
    public CitizensIntegrator(final Compatibility compatibility) {
        this.compatibility = compatibility;
        plugin = BetonQuest.getInstance();
    }

    /**
     * Gets the move controller used to start and stop NPC movement.
     *
     * @return the move controller of this NPC integration
     */
    public static CitizensMoveController getCitizensMoveInstance() {
        return citizensMoveController;
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener(plugin, npcRegistry);
        server.getPluginManager().registerEvents(citizensWalkingListener, plugin);

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin, plugin.getQuestTypeApi(), citizensWalkingListener);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.objective().register("npckill", new NPCKillObjectiveFactory(npcRegistry));

        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        final PluginManager manager = server.getPluginManager();
        manager.registerEvents(citizensMoveController, plugin);

        final EventTypeRegistry eventTypes = questRegistries.event();
        final FeatureApi featureApi = plugin.getFeatureApi();
        eventTypes.register("npcmove", new CitizensMoveEventFactory(featureApi, data, citizensMoveController));
        eventTypes.registerCombined("npcstop", new CitizensStopEventFactory(featureApi, data, citizensMoveController));

        final FeatureRegistries featureRegistries = plugin.getFeatureRegistries();
        final ConversationIORegistry conversationIOTypes = featureRegistries.conversationIO();
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();
        final FontRegistry fontRegistry = plugin.getFontRegistry();
        final ConversationColors colors = plugin.getConversationColors();
        conversationIOTypes.register("chest", new CitizensInventoryConvIOFactory(loggerFactory, fontRegistry, colors, pluginConfig, false));
        conversationIOTypes.register("combined", new CitizensInventoryConvIOFactory(loggerFactory, fontRegistry, colors, pluginConfig, true));

        final NpcTypeRegistry npcTypes = featureRegistries.npc();
        manager.registerEvents(new CitizensInteractCatcher(plugin.getProfileProvider(), npcTypes, npcRegistry,
                citizensMoveController), plugin);
        npcTypes.register("citizens", new CitizensNpcFactory(npcRegistry));
        npcTypes.registerIdentifier(new CitizensReverseIdentifier(npcRegistry));
    }

    @Override
    public void postHook() {
        if (compatibility.getHooked().contains("ProtocolLib")) {
            CitizensHider.start(plugin);
        } else {
            plugin.getLoggerFactory().create(CitizensIntegrator.class)
                    .warn("ProtocolLib Integration not found! Hiding Citizens NPCs won't be available.");
        }
    }

    @Override
    public void reload() {
        if (CitizensHider.getInstance() != null) {
            CitizensHider.start(plugin);
        }
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
