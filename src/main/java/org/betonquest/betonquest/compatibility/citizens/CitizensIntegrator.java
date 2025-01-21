package org.betonquest.betonquest.compatibility.citizens;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.condition.distance.NPCDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.citizens.condition.location.NPCLocationConditionFactory;
import org.betonquest.betonquest.compatibility.citizens.condition.region.NPCRegionConditionFactory;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.citizens.event.teleport.NPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.citizens.objective.NPCInteractObjective;
import org.betonquest.betonquest.compatibility.citizens.objective.NPCKillObjective;
import org.betonquest.betonquest.compatibility.citizens.objective.NPCRangeObjective;
import org.betonquest.betonquest.compatibility.citizens.variable.npc.CitizensVariableFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.ObjectiveTypeRegistry;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Integrator for Citizens.
 */
@SuppressWarnings("NullAway.Init")
public class CitizensIntegrator implements Integrator {
    /**
     * The active integrator instance.
     */
    private static CitizensIntegrator instance;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Starts conversations on NPC interaction.
     */
    private CitizensConversationStarter citizensConversationStarter;

    /**
     * Handles NPC movement of the {@link CitizensMoveEvent}.
     */
    private CitizensMoveController citizensMoveController;

    /**
     * The default Constructor.
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public CitizensIntegrator() {
        instance = this;
        plugin = BetonQuest.getInstance();
    }

    /**
     * Gets the move controller used to start and stop NPC movement.
     *
     * @return the move controller of this NPC integration
     */
    public static CitizensMoveController getCitizensMoveInstance() {
        return instance.citizensMoveController;
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class));
        citizensConversationStarter = new CitizensConversationStarter(loggerFactory, loggerFactory.create(CitizensConversationStarter.class), citizensMoveController);
        new CitizensWalkingListener();

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("npckill", NPCKillObjective.class);
        objectiveTypes.register("npcinteract", NPCInteractObjective.class);
        objectiveTypes.register("npcrange", NPCRangeObjective.class);

        final Server server = plugin.getServer();
        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        server.getPluginManager().registerEvents(citizensMoveController, plugin);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("movenpc", new CitizensMoveEventFactory(data, citizensMoveController));
        eventTypes.register("stopnpc", new CitizensStopEventFactory(data, citizensMoveController));
        eventTypes.registerCombined("teleportnpc", new NPCTeleportEventFactory(data));

        final ConversationIORegistry conversationIOTypes = plugin.getFeatureRegistries().conversationIO();
        conversationIOTypes.register("chest", CitizensInventoryConvIO.class);
        conversationIOTypes.register("combined", CitizensInventoryConvIO.CitizensCombined.class);

        questRegistries.variable().register("citizen", new CitizensVariableFactory());

        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("npcdistance", new NPCDistanceConditionFactory(data, loggerFactory));
        conditionTypes.registerCombined("npclocation", new NPCLocationConditionFactory(data));
    }

    @Override
    public void postHook() {
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
            plugin.getQuestRegistries().event().register("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        if (Compatibility.getHooked().contains("WorldGuard")) {
            final Server server = plugin.getServer();
            final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
            plugin.getQuestRegistries().condition().register("npcregion", new NPCRegionConditionFactory(data));
        }
    }

    @Override
    public void reload() {
        citizensConversationStarter.reload();
        if (NPCHider.getInstance() != null) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
        }
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
