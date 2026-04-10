package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.Translations;
import org.betonquest.betonquest.id.menu.MenuIdentifierFactory;
import org.betonquest.betonquest.id.menu.MenuItemIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link RPGMenu}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class RPGMenuComponent extends AbstractCoreComponent {

    /**
     * Create a new RPGMenuComponent.
     */
    public RPGMenuComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, ConfigAccessor.class,
                ProfileProvider.class, ArgumentParsers.class, Instructions.class, Identifiers.class,
                ParsedSectionTextCreator.class, PluginMessage.class, Reloader.class,
                ActionManager.class, ConditionManager.class, ProcessorDataLoader.class,
                ActionRegistry.class, ConditionRegistry.class, ObjectiveRegistry.class, PlaceholderRegistry.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(RPGMenu.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final Translations translations = getDependency(PluginMessage.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ActionManager actionManager = getDependency(ActionManager.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ActionRegistry actionRegistry = getDependency(ActionRegistry.class);
        final ConditionRegistry conditionRegistry = getDependency(ConditionRegistry.class);
        final ObjectiveRegistry objectiveRegistry = getDependency(ObjectiveRegistry.class);
        final PlaceholderRegistry placeholderRegistry = getDependency(PlaceholderRegistry.class);
        final ArgumentParsers argumentParsers = getDependency(ArgumentParsers.class);
        final Reloader reloader = getDependency(Reloader.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final MenuIdentifierFactory menuIdentifierFactory = new MenuIdentifierFactory(packManager);
        identifiers.register(MenuIdentifier.class, menuIdentifierFactory);
        final MenuItemIdentifierFactory menuItemIdentifierFactory = new MenuItemIdentifierFactory(packManager);
        identifiers.register(MenuItemIdentifier.class, menuItemIdentifierFactory);
        final RPGMenu rpgMenu = new RPGMenu(loggerFactory.create(RPGMenu.class), loggerFactory, instructions, config,
                translations, parsedSectionTextCreator, profileProvider, argumentParsers,
                menuIdentifierFactory, menuItemIdentifierFactory, actionRegistry, conditionRegistry,
                objectiveRegistry, placeholderRegistry, actionManager, conditionManager);

        dependencyProvider.take(RPGMenu.class, rpgMenu);
        reloader.register(ReloadPhase.PROFILES, rpgMenu::syncCommands);

        processorDataLoader.addProcessor(rpgMenu.getMenuProcessor());
        processorDataLoader.addProcessor(rpgMenu.getMenuItemProcessor());
    }
}
