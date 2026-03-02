package org.betonquest.betonquest.kernel.component;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.Server;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PlayerDataStorage}.
 */
public class PlayerDataStorageComponent extends AbstractCoreComponent {

    /**
     * Create a new PlayerDataStorageComponent.
     */
    public PlayerDataStorageComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, ConfigAccessor.class, Saver.class, Identifiers.class,
                ProfileProvider.class, ObjectiveProcessor.class, Server.class, Reloader.class);
    }

    @Override
    public boolean requires(final Class<?> type) {
        return JournalFactory.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(PlayerDataFactory.class, PlayerDataStorage.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Saver saver = getDependency(Saver.class);
        final ObjectiveProcessor objectiveProcessor = getDependency(ObjectiveProcessor.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Server server = getDependency(Server.class);
        final Reloader reloader = getDependency(Reloader.class);

        final PlayerDataFactory playerDataFactory = new PlayerDataFactory(loggerFactory, saver, server,
                identifiers, objectiveProcessor, Suppliers.memoize(() -> getDependency(JournalFactory.class)));
        final PlayerDataStorage playerDataStorage = new PlayerDataStorage(loggerFactory.create(PlayerDataStorage.class), config,
                playerDataFactory, objectiveProcessor, profileProvider);

        dependencyProvider.take(PlayerDataFactory.class, playerDataFactory);
        dependencyProvider.take(PlayerDataStorage.class, playerDataStorage);
        reloader.register(ReloadPhase.PROFILES, () -> {
            playerDataStorage.startObjectives();
            playerDataStorage.reloadProfiles(profileProvider.getOnlineProfiles());
        });
    }
}
