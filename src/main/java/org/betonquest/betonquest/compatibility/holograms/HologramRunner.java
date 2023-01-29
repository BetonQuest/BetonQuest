package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups together all holograms with same update interval and updates them with the same {@link BukkitRunnable}
 */
public final class HologramRunner {
    /**
     * Static HashMap of all active runners. The key is the interval of the runner, the value is the runner itself.
     */
    private static final Map<Integer, HologramRunner> RUNNERS = new HashMap<>();

    /**
     * ArrayList of all holograms of a single runner.
     */
    private final List<HologramWrapper> holograms = new ArrayList<>();
    /**
     * Times the periodic execution of content and visibility refresh.
     */
    private final BukkitRunnable runnable;

    /**
     * Creates a new instance of the HologramRunner with the specified interval.
     *
     * @param interval Interval in ticks
     */
    private HologramRunner(final int interval) {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                holograms.forEach(h -> {
                    h.updateVisibility();
                    h.updateContent();
                });
            }
        };
        runnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), 20, interval);
    }

    /**
     * Adds a new HologramWrapper to the execution cycle. Decides whether to create a new runner or add the
     * Hologram to an existing runner that shares the same cycle in ticks.
     *
     * @param hologram Hologram to be added
     */
    @SuppressWarnings("PMD.CommentDefaultAccessModifier")
    static void addHologram(final HologramWrapper hologram) {
        RUNNERS.computeIfAbsent(hologram.interval(),
                        k -> new HologramRunner(hologram.interval()))
                .addRunnerHologram(hologram);
        hologram.updateVisibility();
        hologram.initialiseContent();
    }

    /**
     * Refreshes all HologramRunners for a single player
     *
     * @param profile The online player's profile
     */
    public static void refresh(final OnlineProfile profile) {
        RUNNERS.values().forEach(hologramRunner -> hologramRunner.refreshRunner(profile));
    }

    /**
     * Cancels hologram updating loop and removes all BetonQuest-registered holograms.
     */
    public static void cancel() {
        RUNNERS.values().forEach(HologramRunner::cancelRunner);
        RUNNERS.clear();
    }

    /**
     * Adds a new hologram to the runner.
     *
     * @param hologramWrapper The hologram to add
     */
    private void addRunnerHologram(final HologramWrapper hologramWrapper) {
        holograms.add(hologramWrapper);
    }

    /**
     * Refreshes all HologramRunners for a single player
     *
     * @param profile The online player's profile
     */
    private void refreshRunner(final OnlineProfile profile) {
        holograms.forEach(wrapper -> wrapper.updateVisibilityForPlayer(profile));
    }

    private void cancelRunner() {
        runnable.cancel();
        holograms.forEach(hologramWrapper -> hologramWrapper.holograms().forEach(BetonHologram::delete));
    }
}
