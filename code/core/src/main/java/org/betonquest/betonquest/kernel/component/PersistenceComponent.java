package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.data.PersistentDataHolder;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Persistence}.
 */
public class PersistenceComponent extends AbstractCoreComponent {

    @Override
    public Set<Class<?>> requires() {
        return Set.of(GlobalData.class, PlayerDataStorage.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Persistence.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final GlobalData globalData = getDependency(GlobalData.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);

        final DefaultPersistence persistence = new DefaultPersistence(globalData, playerDataStorage);

        dependencyProvider.take(DefaultPersistence.class, persistence);
    }

    /**
     * The default implementation of {@link Persistence}.
     *
     * @param globalData        the global data
     * @param playerDataStorage the player data storage
     */
    /* default */ record DefaultPersistence(GlobalData globalData,
                                            PlayerDataStorage playerDataStorage) implements Persistence {

        @Override
        public PersistentDataHolder global() {
            return globalData;
        }

        @Override
        public PersistentDataHolder profile(final Profile profile) {
            return playerDataStorage.get(profile);
        }
    }
}
