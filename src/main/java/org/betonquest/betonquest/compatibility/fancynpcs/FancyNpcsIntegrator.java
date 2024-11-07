package org.betonquest.betonquest.compatibility.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fancynpcs.condition.distance.NPCDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.fancynpcs.condition.location.NPCLocationConditionFactory;
import org.betonquest.betonquest.compatibility.fancynpcs.condition.region.NPCRegionConditionFactory;
import org.betonquest.betonquest.compatibility.fancynpcs.event.teleport.NPCTeleportEventFactory;
import org.betonquest.betonquest.compatibility.fancynpcs.objective.NPCInteractObjective;
import org.betonquest.betonquest.compatibility.fancynpcs.objective.NPCKillObjective;
import org.betonquest.betonquest.compatibility.fancynpcs.objective.NPCRangeObjective;
import org.betonquest.betonquest.compatibility.fancynpcs.variable.npc.FancyNpcsVariableFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.compatibility.protocollib.hider.UpdateVisibilityNowEvent;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Integrator for FancyNpcs.
 */
@SuppressWarnings("NullAway.Init")
public class FancyNpcsIntegrator implements Integrator {
    /**
     * The active integrator instance.
     */
    private static FancyNpcsIntegrator instance;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Starts conversations on NPC interaction.
     */
    private FancyNpcsConversationStarter fancyNpcsConversationStarter;

    /**
     * The default Constructor.
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public FancyNpcsIntegrator() {
        instance = this;
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        fancyNpcsConversationStarter = new FancyNpcsConversationStarter(loggerFactory, loggerFactory.create(FancyNpcsConversationStarter.class));

        plugin.registerObjectives("npckill", NPCKillObjective.class);
        plugin.registerObjectives("npcinteract", NPCInteractObjective.class);
        plugin.registerObjectives("npcrange", NPCRangeObjective.class);

        final Server server = plugin.getServer();
        final BukkitScheduler scheduler = server.getScheduler();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, scheduler, plugin);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
        eventTypes.registerCombined("teleportnpc", new NPCTeleportEventFactory(data));

        plugin.registerConversationIO("chest", FancyNpcsInventoryConvIO.class);
        plugin.registerConversationIO("combined", FancyNpcsInventoryConvIO.FancyNpcsCombined.class);

        questRegistries.getVariableTypes().register("fancynpc", new FancyNpcsVariableFactory(loggerFactory));

        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("npcdistance", new NPCDistanceConditionFactory(data, loggerFactory));
        conditionTypes.registerCombined("npclocation", new NPCLocationConditionFactory(data));
    }

    @Override
    public void postHook() {
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
            plugin.getQuestRegistries().getEventTypes().register("updatevisibility", UpdateVisibilityNowEvent.class);
        }
        if (Compatibility.getHooked().contains("WorldGuard")) {
            final Server server = plugin.getServer();
            final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
            plugin.getQuestRegistries().getConditionTypes().register("npcregion", new NPCRegionConditionFactory(data));
        }
    }

    @Override
    public void reload() {
        fancyNpcsConversationStarter.reload();
        if (NPCHider.getInstance() != null) {
            NPCHider.start(plugin.getLoggerFactory().create(NPCHider.class));
        }
    }

    /**
     * Clean up everything.
     */
    @Override
    public void close() {

    }
}
