package org.betonquest.betonquest.api.reload;

/**
 * The {@link Reloader} is a registry for {@link Runnable}s to load them in the order of {@link ReloadPhase}s.
 */
public interface Reloader {

    /**
     * Registers a {@link Runnable} to be reloaded during the next reload.
     *
     * @param reloadPhase the phase to register the reloadable for
     * @param reloadable  the reloadable to register
     */
    void register(ReloadPhase reloadPhase, Runnable reloadable);

    /**
     * Reloads all registered {@link Runnable}s for all {@link ReloadPhase}s.
     */
    void reload();
}
