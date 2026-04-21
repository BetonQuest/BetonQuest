package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.listener.CustomDropListener;
import org.betonquest.betonquest.listener.JoinQuitListener;
import org.betonquest.betonquest.listener.MobKillListener;
import org.betonquest.betonquest.listener.QuestItemConvertListener;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all listeners.
 */
public class ListenersComponent extends AbstractCoreComponent {

    /**
     * Create a new ListenersComponent.
     */
    public ListenersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class,
                BetonQuestLoggerFactory.class, ProfileProvider.class, FileConfigAccessor.class,
                PlayerDataStorage.class, Localizations.class,
                ItemIdentifierFactory.class, ObjectiveProcessor.class, ItemManager.class, Conversations.class, Updater.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final FileConfigAccessor config = getDependency(FileConfigAccessor.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final Localizations localizations = getDependency(Localizations.class);
        final ItemIdentifierFactory itemIdentifierFactory = getDependency(ItemIdentifierFactory.class);
        final ObjectiveProcessor objectiveProcessor = getDependency(ObjectiveProcessor.class);
        final ItemManager itemManager = getDependency(ItemManager.class);
        final Conversations conversations = getDependency(Conversations.class);
        final Updater updater = getDependency(Updater.class);

        List.of(
                new CombatTagger(profileProvider, config.getInt("conversation.damage.combat_delay")),
                new MobKillListener(profileProvider),
                new CustomDropListener(loggerFactory.create(CustomDropListener.class), itemManager, itemIdentifierFactory),
                new QuestItemHandler(config, playerDataStorage, profileProvider),
                new QuestItemConvertListener(loggerFactory.create(QuestItemConvertListener.class),
                        () -> config.getBoolean("item.quest.update_legacy_on_join"), localizations, profileProvider),
                new JoinQuitListener(config, objectiveProcessor, playerDataStorage,
                        conversations, profileProvider, updater)
        ).forEach(listener -> pluginManager.registerEvents(listener, plugin));
    }
}
