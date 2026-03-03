package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.font.DefaultFontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveAction;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensStopActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.objective.NPCKillObjectiveFactory;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

/**
 * Integrator for Citizens.
 */
public class CitizensIntegrator implements Integrator {

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
    public void hook(final BetonQuestApi api) throws HookException {
        final IdentifierFactory<NpcIdentifier> npcIdentifierFactory;
        try {
            npcIdentifierFactory = api.identifiers().getFactory(NpcIdentifier.class);
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not load npc identifier factory while hooking into citizens.", e);
        }

        final NPCRegistry citizensNpcRegistry = CitizensAPI.getNPCRegistry();
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener(plugin, citizensNpcRegistry);
        final PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(citizensWalkingListener, plugin);

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin, api.actions().manager(), citizensWalkingListener);

        final Instructions instructionApi = api.instructions();
        final ActionRegistry actionRegistry = api.actions().registry();
        manager.registerEvents(citizensMoveController, plugin);
        final CitizensArgument citizensArgument = new CitizensArgument(instructionApi, npcIdentifierFactory);
        api.objectives().registry().register("npckill", new NPCKillObjectiveFactory(citizensArgument, instructionApi, citizensNpcRegistry));
        actionRegistry.register("npcmove", new CitizensMoveActionFactory(api.npcs().manager(), citizensArgument, citizensMoveController));
        actionRegistry.registerCombined("npcstop", new CitizensStopActionFactory(api.npcs().manager(), citizensArgument, citizensMoveController));

        final FeatureRegistry<ConversationIOFactory> conversationIORegistry = BetonQuest.getInstance().getComponentLoader().get(ConversationIORegistry.class);
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();
        final DefaultFontRegistry fontRegistry = plugin.getFontRegistry();
        final ConversationColors colors = plugin.getConversationColors();
        conversationIORegistry.register("chest", new CitizensInventoryConvIOFactory(loggerFactory,
                fontRegistry, colors, pluginConfig, plugin, plugin.getServer().getPluginManager(), api.instructions(),
                plugin.getPluginMessage(), api.items().manager(), api.profiles(), api.conversations(), false));
        conversationIORegistry.register("combined", new CitizensInventoryConvIOFactory(loggerFactory,
                fontRegistry, colors, pluginConfig, plugin, plugin.getServer().getPluginManager(), api.instructions(),
                plugin.getPluginMessage(), api.items().manager(), api.profiles(), api.conversations(), true));

        final NpcRegistry npcRegistry = api.npcs().registry();
        manager.registerEvents(new CitizensInteractCatcher(plugin.getProfileProvider(), npcRegistry, citizensNpcRegistry,
                citizensMoveController), plugin);
        npcRegistry.register("citizens", new CitizensNpcFactory(citizensNpcRegistry));
        npcRegistry.registerIdentifier(new CitizensReverseIdentifier(citizensNpcRegistry));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(citizensMoveController);
    }
}
