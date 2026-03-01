package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for functions to call post-enable.
 */
public class PostEnableComponent extends AbstractCoreComponent {

    /**
     * Create a new PostEnableComponent.q
     */
    public PostEnableComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class, ConfigAccessor.class,
                Compatibility.class, PlayerDataStorage.class, ProfileProvider.class, QuestPackageManager.class,
                PlayerHider.class, RPGMenu.class, Conversations.class, ProcessorDataLoader.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BukkitScheduler scheduler = getDependency(BukkitScheduler.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Compatibility compatibility = getDependency(Compatibility.class);
        final PlayerDataStorage dataStorage = getDependency(PlayerDataStorage.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final PlayerHider playerHider = getDependency(PlayerHider.class);
        final RPGMenu menu = getDependency(RPGMenu.class);
        final Conversations conversations = getDependency(Conversations.class);
        final QuestPackageManager packageManager = getDependency(QuestPackageManager.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            compatibility.postHook();
            new LoadDataEvent(LoadDataEvent.State.PRE_LOAD).callEvent();
            processorDataLoader.loadData(packageManager.getPackages().values());
            new LoadDataEvent(LoadDataEvent.State.POST_LOAD).callEvent();
            dataStorage.startObjectives();
            menu.syncCommands();
            dataStorage.initProfiles(profileProvider.getOnlineProfiles(), conversations);
            playerHider.start(scheduler, config);
        });
    }
}
