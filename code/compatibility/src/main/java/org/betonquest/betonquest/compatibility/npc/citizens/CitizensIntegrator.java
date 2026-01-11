package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureRegistries;
import org.betonquest.betonquest.api.kernel.FeatureRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveAction;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensMoveController;
import org.betonquest.betonquest.compatibility.npc.citizens.action.move.CitizensStopActionFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.objective.NPCKillObjectiveFactory;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
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
        final NPCRegistry citizensNpcRegistry = CitizensAPI.getNPCRegistry();
        final CitizensWalkingListener citizensWalkingListener = new CitizensWalkingListener(plugin, citizensNpcRegistry);
        final PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(citizensWalkingListener, plugin);

        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        citizensMoveController = new CitizensMoveController(loggerFactory.create(CitizensMoveController.class),
                plugin, api.getQuestTypeApi(), citizensWalkingListener);

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        try {
            questRegistries.objective().register("npckill", new NPCKillObjectiveFactory(api, citizensNpcRegistry));
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not register 'npckill' objective while hooking citizens.", e);
        }

        manager.registerEvents(citizensMoveController, plugin);

        final ActionRegistry actionRegistry = questRegistries.action();
        try {
            actionRegistry.register("npcmove", new CitizensMoveActionFactory(api, citizensMoveController));
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not register 'npcmove' action while hooking citizens.", e);
        }
        try {
            actionRegistry.registerCombined("npcstop", new CitizensStopActionFactory(api, citizensMoveController));
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not register 'npcstop' action while hooking citizens.", e);
        }

        final FeatureRegistries featureRegistries = api.getFeatureRegistries();
        final FeatureRegistry<ConversationIOFactory> conversationIORegistry = featureRegistries.conversationIO();
        final Placeholders placeholders = api.getQuestTypeApi().placeholders();
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();
        final FontRegistry fontRegistry = plugin.getFontRegistry();
        final ConversationColors colors = plugin.getConversationColors();
        conversationIORegistry.register("chest", new CitizensInventoryConvIOFactory(loggerFactory,
                placeholders, api.getQuestPackageManager(), fontRegistry, colors, pluginConfig, false));
        conversationIORegistry.register("combined", new CitizensInventoryConvIOFactory(loggerFactory,
                placeholders, api.getQuestPackageManager(), fontRegistry, colors, pluginConfig, true));

        final NpcRegistry npcRegistry = featureRegistries.npc();
        manager.registerEvents(new CitizensInteractCatcher(plugin, plugin.getProfileProvider(), npcRegistry, citizensNpcRegistry,
                citizensMoveController), plugin);
        npcRegistry.register("citizens", new CitizensNpcFactory(plugin, citizensNpcRegistry));
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
