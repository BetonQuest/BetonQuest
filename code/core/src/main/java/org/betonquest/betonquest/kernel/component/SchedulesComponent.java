package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.schedule.ScheduleIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.schedule.ActionScheduling;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ScheduleRegistry}.
 */
public class SchedulesComponent extends AbstractCoreComponent {

    /**
     * Create a new SchedulesComponent.
     */
    public SchedulesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class, Instructions.class,
                ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ScheduleIdentifierFactory.class, ScheduleRegistry.class, ActionScheduling.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final ScheduleIdentifierFactory scheduleIdentifierFactory = new ScheduleIdentifierFactory(questPackageManager);
        identifiers.register(ScheduleIdentifier.class, scheduleIdentifierFactory);
        final ScheduleRegistry scheduleRegistry = new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class));
        final ActionScheduling scheduleProcessor = new ActionScheduling(loggerFactory.create(ActionScheduling.class, "Schedules"),
                instructions, scheduleRegistry, scheduleIdentifierFactory);

        dependencyProvider.take(ScheduleIdentifierFactory.class, scheduleIdentifierFactory);
        dependencyProvider.take(ScheduleRegistry.class, scheduleRegistry);
        dependencyProvider.take(ActionScheduling.class, scheduleProcessor);

        processorDataLoader.addProcessor(scheduleProcessor);
    }
}
