package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.conversation.ConversationIdentifierFactory;
import org.betonquest.betonquest.id.npc.NpcIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Npcs}.
 */
public class NpcsComponent extends AbstractCoreComponent {

    /**
     * Create a new NpcsComponent.
     */
    public NpcsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class, ConfigAccessor.class,
                Saver.class, PluginMessage.class, Instructions.class, Identifiers.class,
                ConversationProcessor.class, ActionManager.class, ConditionManager.class,
                ConversationIdentifierFactory.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(NpcIdentifierFactory.class, NpcTypeRegistry.class, NpcProcessor.class, DefaultNpcHider.class, DefaultNpcs.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConversationProcessor conversationProcessor = getDependency(ConversationProcessor.class);
        final ActionManager actionManager = getDependency(ActionManager.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ConversationIdentifierFactory conversationIdentifierFactory = getDependency(ConversationIdentifierFactory.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Saver saver = getDependency(Saver.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final NpcIdentifierFactory npcIdentifierFactory = new NpcIdentifierFactory(questPackageManager);
        identifiers.register(NpcIdentifier.class, npcIdentifierFactory);
        final NpcTypeRegistry npcTypeRegistry = new NpcTypeRegistry(loggerFactory.create(NpcTypeRegistry.class), instructions);
        final NpcProcessor npcProcessor = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, plugin,
                npcIdentifierFactory, conversationIdentifierFactory, npcTypeRegistry, pluginMessage,
                profileProvider, actionManager, conditionManager, conversationProcessor.getStarter(), instructions,
                identifiers, saver, config, conversationProcessor);

        dependencyProvider.take(NpcIdentifierFactory.class, npcIdentifierFactory);
        dependencyProvider.take(NpcTypeRegistry.class, npcTypeRegistry);
        dependencyProvider.take(NpcProcessor.class, npcProcessor);
        dependencyProvider.take(DefaultNpcHider.class, npcProcessor.getNpcHider());
        dependencyProvider.take(DefaultNpcs.class, new DefaultNpcs(npcProcessor, npcTypeRegistry));

        processorDataLoader.addProcessor(npcProcessor);
    }

    /**
     * Default implementation of the {@link Npcs} service.
     *
     * @param manager  the npc manager
     * @param registry the npc registry
     */
    /* default */ record DefaultNpcs(NpcManager manager, NpcRegistry registry) implements Npcs {

    }
}
