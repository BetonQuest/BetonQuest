package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ActionTypeRegistry}.
 */
public class ActionsComponent extends AbstractCoreComponent {

    /**
     * Create a new ActionsComponent.
     */
    public ActionsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class,
                Identifiers.class, ConditionProcessor.class, Instructions.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ActionIdentifierFactory.class, ActionTypeRegistry.class, ActionProcessor.class, DefaultActions.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final ActionIdentifierFactory actionIdentifierFactory = new ActionIdentifierFactory(questPackageManager);
        identifiers.register(ActionIdentifier.class, actionIdentifierFactory);
        final ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry(loggerFactory.create(ActionTypeRegistry.class), loggerFactory, conditionProcessor);
        final ActionProcessor actionProcessor = new ActionProcessor(loggerFactory.create(ActionProcessor.class),
                actionIdentifierFactory, actionTypeRegistry, bukkitScheduler, instructions, plugin);

        dependencyProvider.take(ActionIdentifierFactory.class, actionIdentifierFactory);
        dependencyProvider.take(ActionTypeRegistry.class, actionTypeRegistry);
        dependencyProvider.take(ActionProcessor.class, actionProcessor);
        dependencyProvider.take(DefaultActions.class, new DefaultActions(actionProcessor, actionTypeRegistry));

        processorDataLoader.addProcessor(actionProcessor);
    }

    /**
     * Default implementation of the {@link Actions} service.
     *
     * @param manager  the action manager
     * @param registry the action registry
     */
    /* default */ record DefaultActions(ActionManager manager, ActionRegistry registry) implements Actions {

    }
}
