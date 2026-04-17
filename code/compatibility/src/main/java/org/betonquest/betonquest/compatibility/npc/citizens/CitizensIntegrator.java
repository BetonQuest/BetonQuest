package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.BukkitManager;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveAction;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensStopActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.objective.NPCKillObjectiveFactory;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.bukkit.event.HandlerList;

/**
 * Integrator for Citizens.
 */
public class CitizensIntegrator implements Integration {

    /**
     * Handles NPC movement of the {@link CitizensMoveAction}.
     */
    @SuppressWarnings("NullAway.Init")
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
    public void enable(final BetonQuestApi api) throws QuestException {
        final IdentifierFactory<NpcIdentifier> npcIdentifierFactory = api.identifiers().getFactory(NpcIdentifier.class);

        final NPCRegistry citizensNpcRegistry = CitizensAPI.getNPCRegistry();
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener(plugin, citizensNpcRegistry);
        final BukkitManager bukkitManager = api.bukkit();
        bukkitManager.registerEvents(citizensWalkingListener);

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin, api.actions().manager(), citizensWalkingListener);

        final Instructions instructionApi = api.instructions();
        final ActionRegistry actionRegistry = api.actions().registry();
        bukkitManager.registerEvents(citizensMoveController);
        final CitizensArgument citizensArgument = new CitizensArgument(instructionApi, npcIdentifierFactory);
        api.objectives().registry().register("npckill", new NPCKillObjectiveFactory(citizensArgument, instructionApi, citizensNpcRegistry));
        actionRegistry.register("npcmove", new CitizensMoveActionFactory(api.npcs().manager(), citizensArgument, citizensMoveController));
        actionRegistry.registerCombined("npcstop", new CitizensStopActionFactory(api.npcs().manager(), citizensArgument, citizensMoveController));

        final CoreComponentLoader componentLoader = plugin.getComponentLoader();
        final FeatureRegistry<ConversationIOFactory> conversationIORegistry = componentLoader.get(ConversationIORegistry.class);
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();
        final ConversationColors colors = componentLoader.get(ConversationColors.class);
        conversationIORegistry.register("chest", new CitizensInventoryConvIOFactory(loggerFactory,
                api.fonts(), colors, pluginConfig, plugin, plugin.getServer().getPluginManager(), api.instructions(),
                api.localizations(), api.items().manager(), api.profiles(), api.conversations(), false));
        conversationIORegistry.register("combined", new CitizensInventoryConvIOFactory(loggerFactory,
                api.fonts(), colors, pluginConfig, plugin, plugin.getServer().getPluginManager(), api.instructions(),
                api.localizations(), api.items().manager(), api.profiles(), api.conversations(), true));

        final NpcRegistry npcRegistry = api.npcs().registry();
        bukkitManager.registerEvents(new CitizensInteractCatcher(api.profiles(), npcRegistry, citizensNpcRegistry,
                citizensMoveController));
        npcRegistry.register("citizens", new CitizensNpcFactory(citizensNpcRegistry));
        npcRegistry.registerIdentifier(new CitizensReverseIdentifier(citizensNpcRegistry));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
