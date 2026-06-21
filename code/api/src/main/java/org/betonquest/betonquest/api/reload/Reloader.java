package org.betonquest.betonquest.api.reload;

import org.jetbrains.annotations.Contract;

/**
 * The {@link Reloader} is a registry for {@link Runnable}s to load them in the order of {@link ReloadPhase}s.
 *
 * @since 3.0.0
 */
public interface Reloader {

    /**
     * Registers a {@link Runnable} to be reloaded during the next reload.
     *
     * @param reloadPhase the phase to register the reloadable for
     * @param reloadable  the reloadable to register
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void register(ReloadPhase reloadPhase, Runnable reloadable);

    /**
     * Reloads all registered {@link Runnable}s for all {@link ReloadPhase}s.
     *
     * @since 3.0.0
     */
    void reload();
}
