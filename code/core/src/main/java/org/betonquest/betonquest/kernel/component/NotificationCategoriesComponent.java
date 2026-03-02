package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.notify.Notify;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Notify}.
 */
public class NotificationCategoriesComponent extends AbstractCoreComponent {

    /**
     * Create a new NotificationCategoriesComponent.
     */
    public NotificationCategoriesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(ConfigAccessor.class, QuestPackageManager.class, Reloader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final FileConfigAccessor config = getDependency(FileConfigAccessor.class);
        final Reloader reloader = getDependency(Reloader.class);
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);

        Notify.load(config, packManager.getPackages().values());

        reloader.register(ReloadPhase.INTEGRATION, () -> Notify.load(config, packManager.getPackages().values()));
    }
}
