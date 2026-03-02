package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.id.placeholder.PlaceholderIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Placeholders}.
 */
public class PlaceholdersComponent extends AbstractCoreComponent {

    /**
     * Create a new PlaceholdersComponent.
     */
    public PlaceholdersComponent() {
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
        return Set.of(PlaceholderIdentifierFactory.class, PlaceholderTypeRegistry.class, PlaceholderProcessor.class, DefaultPlaceholders.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final PlaceholderIdentifierFactory placeholderIdentifierFactory = new PlaceholderIdentifierFactory(questPackageManager);
        identifiers.register(PlaceholderIdentifier.class, placeholderIdentifierFactory);
        final PlaceholderTypeRegistry placeholderTypeRegistry = new PlaceholderTypeRegistry(loggerFactory.create(PlaceholderTypeRegistry.class));
        final PlaceholderProcessor placeholderProcessor = new PlaceholderProcessor(loggerFactory.create(PlaceholderProcessor.class),
                questPackageManager, placeholderTypeRegistry, bukkitScheduler, placeholderIdentifierFactory, instructions, plugin);

        dependencyProvider.take(PlaceholderIdentifierFactory.class, placeholderIdentifierFactory);
        dependencyProvider.take(PlaceholderTypeRegistry.class, placeholderTypeRegistry);
        dependencyProvider.take(PlaceholderProcessor.class, placeholderProcessor);
        dependencyProvider.take(DefaultPlaceholders.class, new DefaultPlaceholders(placeholderProcessor, placeholderTypeRegistry));

        processorDataLoader.addProcessor(placeholderProcessor);
    }

    /**
     * Default implementation of the {@link Placeholders} service.
     *
     * @param manager  the placeholder manager
     * @param registry the placeholder registry
     */
    /* default */ record DefaultPlaceholders(PlaceholderManager manager,
                                             PlaceholderRegistry registry) implements Placeholders {

    }
}
