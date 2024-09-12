package org.betonquest.betonquest.compatibility.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.citizens.objective.NPCKillObjective;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

/**
 * Integrator for Citizens.
 */
@SuppressWarnings("NullAway.Init")
public class CitizensIntegrator implements Integrator {

    /**
     * Handles NPC movement of the {@link CitizensMoveEvent}.
     */
    private static CitizensMoveController citizensMoveController;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default Constructor.
     */
    public CitizensIntegrator() {
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
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener();
        server.getPluginManager().registerEvents(citizensWalkingListener, plugin);

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin.getQuestTypeAPI(), citizensWalkingListener);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.objective().register("npckill", NPCKillObjective.class);

        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        final PluginManager manager = server.getPluginManager();
        manager.registerEvents(citizensMoveController, plugin);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("movenpc", new CitizensMoveEventFactory(data, citizensMoveController));
        eventTypes.register("stopnpc", new CitizensStopEventFactory(data, citizensMoveController));

        final ConversationIORegistry conversationIOTypes = plugin.getFeatureRegistries().conversationIO();
        conversationIOTypes.register("chest", CitizensInventoryConvIO.class);
        conversationIOTypes.register("combined", CitizensInventoryConvIO.CitizensCombined.class);

        final CitizensNpcFactory npcFactory = new CitizensNpcFactory();
        final CitizensInteractCatcher catcher = new CitizensInteractCatcher(npcFactory, citizensMoveController);
        manager.registerEvents(catcher, plugin);
        questRegistries.npc().register("citizens", npcFactory, catcher);
    }

    @Override
    public void postHook() {
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
            final Server server = plugin.getServer();
            final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
            plugin.getQuestRegistries().event().register("updatevisibility", new UpdateVisibilityNowEventFactory(
                    Objects.requireNonNull(NPCHider.getInstance()), plugin.getLoggerFactory(), data));
        }
    }

    @Override
    public void reload() {
        if (NPCHider.getInstance() != null) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
        }
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
