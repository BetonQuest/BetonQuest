package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.compatibility.npcs.citizens.condition.CitizensDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.condition.CitizensLocationConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.condition.CitizensRegionConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.CitizensNPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.move.CitizensMoveEvent;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.move.CitizensMoveEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.move.CitizensStopEventFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.objective.CitizensInteractObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.objective.CitizensRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.objective.NPCKillObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.variable.npc.CitizensVariableFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.function.Supplier;

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

    /**
     * Gets a supplier, which will return a new Citizens {@link BQNPCAdapter}
     * if the {@code npcID} has a valid npc or null.
     *
     * @param npcId the id of the Citizens npc
     * @return the supplier which will return the npc or null if none was found by the npcId
     * @throws InstructionParseException when the id cannot be parsed as positive or zero integer
     */
    public static Supplier<BQNPCAdapter<?>> getSupplier(final String npcId) throws InstructionParseException {
        final int parsedId;
        try {
            parsedId = Integer.parseInt(npcId);
            if (parsedId < 0) {
                throw new InstructionParseException("The NPC ID '" + npcId + "' is not a positive or zero integer");
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("The NPC ID '" + npcId + "' is not a valid integer", e);
        }
        return () -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(parsedId);
            return npc == null ? null : new CitizensBQAdapter(npc);
        };
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class));
        citizensConversationStarter = new CitizensConversationStarter(loggerFactory, loggerFactory.create(CitizensConversationStarter.class), citizensMoveController);
        new CitizensWalkingListener();

        // if ProtocolLib is hooked, start NPCHider
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(loggerFactory.create(NPCHider.class));
            plugin.registerEvents("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", CitizensInteractObjective.class);
        plugin.registerObjectives("npcrange", CitizensRangeObjective.class);

        final Server server = plugin.getServer();
        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        server.getPluginManager().registerEvents(citizensMoveController, plugin);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
        eventTypes.register("movenpc", new CitizensMoveEventFactory(data, citizensMoveController));
        eventTypes.register("stopnpc", new CitizensStopEventFactory(data, citizensMoveController));
        eventTypes.registerCombined("teleportnpc", new CitizensNPCTeleportEventFactory(data));

        plugin.registerConversationIO("chest", CitizensInventoryConvIO.class);
        plugin.registerConversationIO("combined", CitizensInventoryConvIO.CitizensCombined.class);

        final NPCSupplierStandard standard = CitizensIntegrator::getSupplier;
        questRegistries.getVariableTypes().register("citizen", new CitizensVariableFactory(standard, loggerFactory));

        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("npcdistance", new CitizensDistanceConditionFactory(standard, data, loggerFactory));
        conditionTypes.registerCombined("npclocation", new CitizensLocationConditionFactory(standard, data));
    }

    @Override
    public void postHook() {
        if (Compatibility.getHooked().contains("WorldGuard")) {
            final Server server = plugin.getServer();
            final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
            plugin.getQuestRegistries().getConditionTypes().register("npcregion",
                    new CitizensRegionConditionFactory(CitizensIntegrator::getSupplier, data));
        }
    }

    @Override
    public void reload() {
        citizensConversationStarter.reload();
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
