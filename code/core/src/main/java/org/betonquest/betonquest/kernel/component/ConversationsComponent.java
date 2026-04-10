package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.conversation.ConversationIdentifierFactory;
import org.betonquest.betonquest.id.conversation.ConversationOptionIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConversationProcessor} and conversation classes.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ConversationsComponent extends AbstractCoreComponent {

    /**
     * Create a new ConversationsComponent.
     */
    public ConversationsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class, ConfigAccessor.class,
                PluginMessage.class, ActionProcessor.class, ConditionProcessor.class, Instructions.class, Saver.class,
                Identifiers.class, ParsedSectionTextCreator.class, PlaceholderProcessor.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ConversationIdentifierFactory.class, ConversationOptionIdentifierFactory.class,
                ConversationIORegistry.class, InterceptorRegistry.class, ConversationProcessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final PlaceholderProcessor placeholderProcessor = getDependency(PlaceholderProcessor.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Translations translations = getDependency(PluginMessage.class);
        final ActionProcessor actionProcessor = getDependency(ActionProcessor.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Saver saver = getDependency(Saver.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final ConversationIdentifierFactory conversationIdentifierFactory = new ConversationIdentifierFactory(questPackageManager);
        identifiers.register(ConversationIdentifier.class, conversationIdentifierFactory);
        final ConversationOptionIdentifierFactory conversationOptionIdentifierFactory = new ConversationOptionIdentifierFactory(questPackageManager);
        identifiers.register(ConversationOptionIdentifier.class, conversationOptionIdentifierFactory);
        final ConversationIORegistry conversationIORegistry = new ConversationIORegistry(loggerFactory.create(ConversationIORegistry.class));
        final InterceptorRegistry interceptorRegistry = new InterceptorRegistry(loggerFactory.create(InterceptorRegistry.class));
        final ConversationProcessor conversationProcessor = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class),
                loggerFactory, plugin, parsedSectionTextCreator, questPackageManager, placeholderProcessor, profileProvider, config, conversationIORegistry, interceptorRegistry,
                instructions, translations, actionProcessor, conditionProcessor, conversationIdentifierFactory, identifiers, saver);

        dependencyProvider.take(ConversationIdentifierFactory.class, conversationIdentifierFactory);
        dependencyProvider.take(ConversationOptionIdentifierFactory.class, conversationOptionIdentifierFactory);
        dependencyProvider.take(ConversationIORegistry.class, conversationIORegistry);
        dependencyProvider.take(InterceptorRegistry.class, interceptorRegistry);
        dependencyProvider.take(ConversationProcessor.class, conversationProcessor);

        processorDataLoader.addProcessor(conversationProcessor);
    }
}
