package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages pending and delayed interceptors for conversations.
 */
public class ConversationInterceptorManager {

    /**
     * Plugin instance used to schedule delayed tasks.
     */
    private final Plugin plugin;

    /**
     * Map of pending delayed interceptors per profile.
     */
    private final Map<Profile, PendingDelayedInterceptor> pendingInterceptors;

    /**
     * Creates a new ConversationInterceptorManager.
     *
     * @param plugin          the plugin instance
     * @param profileProvider the profile provider to access profiles
     */
    public ConversationInterceptorManager(final Plugin plugin, final ProfileProvider profileProvider) {
        this.plugin = plugin;
        this.pendingInterceptors = new ProfileKeyMap<>(profileProvider, new ConcurrentHashMap<>());
    }

    /**
     * Schedules the end of an interceptor after the given delay ticks.
     *
     * @param profile     the profile of the player
     * @param interceptor the interceptor to end
     * @param delayTicks  the delay in ticks
     */
    public void scheduleInterceptorEnd(final OnlineProfile profile, final Interceptor interceptor, final long delayTicks) {
        if (delayTicks <= 0) {
            endPendingInterceptor(profile, interceptor);
            return;
        }
        transferPendingInterceptor(profile, interceptor);

        final BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                final PendingDelayedInterceptor current = pendingInterceptors.get(profile);
                if (current != null && Objects.equals(current.interceptor(), interceptor)) {
                    pendingInterceptors.remove(profile);
                    interceptor.end();
                }
            }
        }.runTaskLaterAsynchronously(plugin, delayTicks);

        pendingInterceptors.put(profile, new PendingDelayedInterceptor(interceptor, task));
    }

    /**
     * Transfers any pending delayed interceptor to the next interceptor.
     *
     * @param profile         the profile of the player
     * @param nextInterceptor the next interceptor
     */
    public void transferPendingInterceptor(final OnlineProfile profile, final Interceptor nextInterceptor) {
        final PendingDelayedInterceptor pending = pendingInterceptors.remove(profile);
        if (pending != null) {
            pending.task().cancel();
            if (!Objects.equals(pending.interceptor(), nextInterceptor)) {
                pending.interceptor().transferTo(nextInterceptor);
            }
        }
    }

    /**
     * Transfers any pending interceptor to the given interceptor and ends the given interceptor immediately.
     *
     * @param profile     the profile of the player
     * @param interceptor the interceptor to end
     */
    public void endPendingInterceptor(final OnlineProfile profile, final Interceptor interceptor) {
        transferPendingInterceptor(profile, interceptor);
        interceptor.end();
    }

    /**
     * Ends any pending delayed interceptor for the profile.
     *
     * @param profile the profile of the player
     */
    public void cancelPendingInterceptor(final OnlineProfile profile) {
        final PendingDelayedInterceptor pending = pendingInterceptors.remove(profile);
        if (pending != null) {
            pending.task().cancel();
            pending.interceptor().end();
        }
    }

    /**
     * Sends a bypass message through the pending interceptor if present.
     *
     * @param profile the online profile of the player
     * @param message the message to send
     * @return true if a pending interceptor handled the message, false otherwise
     */
    public boolean sendBypassMessage(final OnlineProfile profile, final Component message) {
        final PendingDelayedInterceptor pending = pendingInterceptors.get(profile);
        if (pending != null) {
            pending.interceptor().sendMessage(message);
            return true;
        }
        return false;
    }

    /**
     * Holds a pending delayed interceptor and its scheduled task.
     *
     * @param interceptor the interceptor
     * @param task        the scheduled task
     */
    private record PendingDelayedInterceptor(Interceptor interceptor, BukkitTask task) {

    }
}
