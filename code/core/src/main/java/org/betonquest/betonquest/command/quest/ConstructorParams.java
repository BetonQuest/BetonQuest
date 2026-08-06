package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.history.LogPublishingController;
import org.betonquest.betonquest.web.updater.Updater;

/**
 * The quest command constructor parameters.
 *
 * @param loggerFactory           the logger factory
 * @param configAccessorFactory   the config accessor factory
 * @param playerDataStorage       the player data storage
 * @param profileProvider         the profile provider
 * @param localizations           the Localizations
 * @param updater                 the updater
 * @param compatibility           the compatibility
 * @param connector               the connector
 * @param saver                   the saver
 * @param questPackageManager     the quest package manager
 * @param configAccessor          the config accessor
 * @param logPublishingController the log publishing controller
 * @param playerLogWatcher        the player log watcher
 * @param identifiers             the identifiers
 * @param globalData              the global data
 * @param journalEntryProcessor   the journal entry processor
 * @param itemTypeRegistry        the item type registry
 * @param actionManager           the action manager
 * @param conditionManager        the condition manager
 * @param objectiveManager        the objective manager
 * @param itemManager             the item manager
 * @param reloader                the plugin reloading runnable
 */
public record ConstructorParams(BetonQuestLoggerFactory loggerFactory, ConfigAccessorFactory configAccessorFactory,
                                PlayerDataStorage playerDataStorage, ProfileProvider profileProvider,
                                Localizations localizations, Updater updater, Compatibility compatibility,
                                Connector connector, Saver saver, QuestPackageManager questPackageManager,
                                ConfigAccessor configAccessor, LogPublishingController logPublishingController,
                                PlayerLogWatcher playerLogWatcher, Identifiers identifiers, GlobalData globalData,
                                JournalEntryProcessor journalEntryProcessor,
                                ItemTypeRegistry itemTypeRegistry, ActionManager actionManager,
                                ConditionManager conditionManager,
                                ObjectiveManager objectiveManager, ItemManager itemManager, Reloader reloader) {

}
