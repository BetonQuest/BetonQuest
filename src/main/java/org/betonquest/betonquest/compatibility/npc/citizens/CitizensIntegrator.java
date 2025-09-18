package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.feature.FeatureRegistries;
import org.betonquest.betonquest.api.kernel.FeatureRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.objective.NPCKillObjectiveFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.CitizensHider;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
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
        final NPCRegistry citizensNpcRegistry = CitizensAPI.getNPCRegistry();
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener(plugin, citizensNpcRegistry);
        server.getPluginManager().registerEvents(citizensWalkingListener, plugin);

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin, plugin.getQuestTypeApi(), citizensWalkingListener);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.objective().register("npckill", new NPCKillObjectiveFactory(citizensNpcRegistry));

        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        final PluginManager manager = server.getPluginManager();
        manager.registerEvents(citizensMoveController, plugin);

        final EventRegistry eventRegistry = questRegistries.event();
        final FeatureApi featureApi = plugin.getFeatureApi();
        eventRegistry.register("npcmove", new CitizensMoveEventFactory(featureApi, data, citizensMoveController));
        eventRegistry.registerCombined("npcstop", new CitizensStopEventFactory(featureApi, data, citizensMoveController));

        final FeatureRegistries featureRegistries = plugin.getFeatureRegistries();
        final FeatureRegistry<ConversationIOFactory> conversationIORegistry = featureRegistries.conversationIO();
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();
        final FontRegistry fontRegistry = plugin.getFontRegistry();
        final ConversationColors colors = plugin.getConversationColors();
        conversationIORegistry.register("chest", new CitizensInventoryConvIOFactory(loggerFactory, plugin.getQuestPackageManager(), fontRegistry, colors, pluginConfig, false));
        conversationIORegistry.register("combined", new CitizensInventoryConvIOFactory(loggerFactory, plugin.getQuestPackageManager(), fontRegistry, colors, pluginConfig, true));

        final NpcRegistry npcRegistry = featureRegistries.npc();
        manager.registerEvents(new CitizensInteractCatcher(plugin.getProfileProvider(), npcRegistry, citizensNpcRegistry,
                citizensMoveController), plugin);
        npcRegistry.register("citizens", new CitizensNpcFactory(citizensNpcRegistry));
        npcRegistry.registerIdentifier(new CitizensReverseIdentifier(citizensNpcRegistry));
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
