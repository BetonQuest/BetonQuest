package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.service.DefaultObjectiveServiceProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.id.objective.ObjectiveIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Objectives}.
 */
public class ObjectivesComponent extends AbstractCoreComponent {

    /**
     * Create a new ObjectivesComponent.
     */
    public ObjectivesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class,
                Identifiers.class, ConditionProcessor.class, ActionProcessor.class, Instructions.class,
                ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ObjectiveIdentifierFactory.class, ObjectiveTypeRegistry.class, ObjectiveProcessor.class, DefaultObjectives.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final ActionProcessor actionProcessor = getDependency(ActionProcessor.class);
        final Instructions instructions = getDependency(Instructions.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final DefaultObjectiveServiceProvider objectiveServiceProvider = new DefaultObjectiveServiceProvider(plugin, conditionProcessor, actionProcessor,
                loggerFactory, profileProvider, instructions);
        final ObjectiveIdentifierFactory objectiveIdentifierFactory = new ObjectiveIdentifierFactory(questPackageManager);
        identifiers.register(ObjectiveIdentifier.class, objectiveIdentifierFactory);
        final ObjectiveTypeRegistry objectiveTypeRegistry = new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class));
        final ObjectiveProcessor objectiveProcessor = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class),
                objectiveTypeRegistry, objectiveIdentifierFactory, pluginManager,
                objectiveServiceProvider, instructions, plugin);

        dependencyProvider.take(ObjectiveIdentifierFactory.class, objectiveIdentifierFactory);
        dependencyProvider.take(ObjectiveTypeRegistry.class, objectiveTypeRegistry);
        dependencyProvider.take(ObjectiveProcessor.class, objectiveProcessor);
        dependencyProvider.take(DefaultObjectives.class, new DefaultObjectives(objectiveProcessor, objectiveTypeRegistry));

        processorDataLoader.addProcessor(objectiveProcessor);
    }

    /**
     * Default implementation of the {@link Objectives} service.
     *
     * @param manager  the objective manager
     * @param registry the objective registry
     */
    /* default */ record DefaultObjectives(ObjectiveManager manager, ObjectiveRegistry registry) implements Objectives {

    }
}
