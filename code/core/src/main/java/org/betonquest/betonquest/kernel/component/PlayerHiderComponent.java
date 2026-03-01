package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PlayerHider}.
 */
public class PlayerHiderComponent extends AbstractCoreComponent {

    /**
     * Create a new PlayerHiderComponent.
     */
    public PlayerHiderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BukkitScheduler.class,
                ConfigAccessor.class, QuestPackageManager.class, ProfileProvider.class, BetonQuestLoggerFactory.class,
                Instructions.class, ConditionManager.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(PlayerHider.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BukkitScheduler bukkitScheduler = getDependency(BukkitScheduler.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final Reloader reloader = getDependency(Reloader.class);

        final PlayerHider playerHider = new PlayerHider(plugin, loggerFactory.create(PlayerHider.class), conditionManager, profileProvider);
        playerHider.load(packManager, instructions);

        dependencyProvider.take(PlayerHider.class, playerHider);
        reloader.register(ReloadPhase.PROFILES, () -> {
            playerHider.stop();
            playerHider.load(packManager, instructions);
            playerHider.start(bukkitScheduler, config);
        });
    }
}
