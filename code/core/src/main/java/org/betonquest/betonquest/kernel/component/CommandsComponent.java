package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.command.BackpackCommand;
import org.betonquest.betonquest.command.CancelQuestCommand;
import org.betonquest.betonquest.command.CompassCommand;
import org.betonquest.betonquest.command.JournalCommand;
import org.betonquest.betonquest.command.LangCommand;
import org.betonquest.betonquest.command.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for commands.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CommandsComponent extends AbstractCoreComponent {

    /**
     * Create a new CommandsComponent.
     */
    public CommandsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(JavaPlugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class,
                ProfileProvider.class, PlayerDataFactory.class, PlayerDataStorage.class,
                GlobalData.class, ConfigAccessor.class, PluginMessage.class, Updater.class,
                Compatibility.class, QuestPackageManager.class, Connector.class, Saver.class,
                ItemTypeRegistry.class, JournalEntryProcessor.class, CompassProcessor.class,
                CancelerProcessor.class, Identifiers.class, ItemManager.class, ActionManager.class,
                ConditionManager.class, ObjectiveManager.class, LanguageProvider.class,
                AccumulatingReceiverSelector.class, HistoryHandler.class, Reloader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final JavaPlugin javaPlugin = getDependency(JavaPlugin.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final PlayerDataFactory playerDataFactory = getDependency(PlayerDataFactory.class);
        final GlobalData globalData = getDependency(GlobalData.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final Updater updater = getDependency(Updater.class);
        final Compatibility compatibility = getDependency(Compatibility.class);
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final Connector connector = getDependency(Connector.class);
        final Saver saver = getDependency(Saver.class);
        final ItemTypeRegistry itemRegistry = getDependency(ItemTypeRegistry.class);
        final JournalEntryProcessor journalEntryProcessor = getDependency(JournalEntryProcessor.class);
        final CompassProcessor compassProcessor = getDependency(CompassProcessor.class);
        final CancelerProcessor cancelerProcessor = getDependency(CancelerProcessor.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ItemManager itemManager = getDependency(ItemManager.class);
        final ActionManager actionManager = getDependency(ActionManager.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ObjectiveManager objectiveManager = getDependency(ObjectiveManager.class);
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final AccumulatingReceiverSelector receiverSelector = getDependency(AccumulatingReceiverSelector.class);
        final HistoryHandler debugHistoryHandler = getDependency(HistoryHandler.class);
        final Reloader reloader = getDependency(Reloader.class);

        final PlayerLogWatcher playerLogWatcher = new PlayerLogWatcher(receiverSelector);
        final QuestCommand.ConstructorParams questCommandParams = new QuestCommand.ConstructorParams(loggerFactory,
                configAccessorFactory, playerDataFactory, playerDataStorage, profileProvider, pluginMessage, updater,
                compatibility, connector, saver, questPackageManager, config, debugHistoryHandler,
                playerLogWatcher, identifiers, globalData, journalEntryProcessor,
                itemRegistry, actionManager, conditionManager, objectiveManager, itemManager, reloader);
        final QuestCommand questCommand = new QuestCommand(javaPlugin, loggerFactory.create(QuestCommand.class), questCommandParams);
        javaPlugin.getCommand("betonquest").setExecutor(questCommand);
        javaPlugin.getCommand("betonquest").setTabCompleter(questCommand);
        javaPlugin.getCommand("journal").setExecutor(new JournalCommand(playerDataStorage, profileProvider));
        javaPlugin.getCommand("backpack").setExecutor(new BackpackCommand(javaPlugin, loggerFactory, loggerFactory.create(BackpackCommand.class),
                config, pluginMessage, profileProvider, playerDataStorage, cancelerProcessor,
                compassProcessor, itemManager, identifiers));
        javaPlugin.getCommand("cancelquest").setExecutor(new CancelQuestCommand(javaPlugin, config, pluginMessage, profileProvider,
                loggerFactory, playerDataStorage, cancelerProcessor, compassProcessor,
                identifiers, itemManager));
        javaPlugin.getCommand("compass").setExecutor(new CompassCommand(javaPlugin, loggerFactory,
                config, pluginMessage, profileProvider, playerDataStorage, cancelerProcessor,
                compassProcessor, itemManager, identifiers));
        final LangCommand langCommand = new LangCommand(loggerFactory.create(LangCommand.class), playerDataStorage, pluginMessage, profileProvider, languageProvider);
        javaPlugin.getCommand("questlang").setExecutor(langCommand);
        javaPlugin.getCommand("questlang").setTabCompleter(langCommand);
        javaPlugin.getCommand("betonquestanswer").setTabCompleter((sender, command, label, args) -> List.of());
    }
}
