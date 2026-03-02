package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.condition.ConditionIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Conditions}.
 */
public class ConditionsComponent extends AbstractCoreComponent {

    /**
     * Create a new ConditionsComponent.
     */
    public ConditionsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class,
                Identifiers.class, Instructions.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ConditionIdentifierFactory.class, ConditionTypeRegistry.class, ConditionProcessor.class, DefaultConditions.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final ConditionIdentifierFactory conditionIdentifierFactory = new ConditionIdentifierFactory(questPackageManager);
        identifiers.register(ConditionIdentifier.class, conditionIdentifierFactory);
        final ConditionTypeRegistry conditionTypeRegistry = new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class));
        final ConditionProcessor conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class),
                conditionTypeRegistry, bukkitScheduler, conditionIdentifierFactory, plugin, instructions);

        dependencyProvider.take(ConditionIdentifierFactory.class, conditionIdentifierFactory);
        dependencyProvider.take(ConditionTypeRegistry.class, conditionTypeRegistry);
        dependencyProvider.take(ConditionProcessor.class, conditionProcessor);
        dependencyProvider.take(DefaultConditions.class, new DefaultConditions(conditionProcessor, conditionTypeRegistry));

        processorDataLoader.addProcessor(conditionProcessor);
    }

    /**
     * Default implementation of the {@link Conditions} service.
     *
     * @param manager  the condition manager
     * @param registry the condition registry
     */
    /* default */ record DefaultConditions(ConditionManager manager, ConditionRegistry registry) implements Conditions {

    }
}
