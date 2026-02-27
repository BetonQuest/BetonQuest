package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Default implementation of {@link Reloader}.
 */
public class DefaultReloader implements Reloader {

    /**
     * Contains all registered {@link Runnable}s.
     */
    private final HashMap<ReloadPhase, Collection<Runnable>> reloadableMap;

    /**
     * Create a new DefaultReloader.
     */
    public DefaultReloader() {
        this.reloadableMap = new HashMap<>();
    }

    @Override
    public void register(final ReloadPhase reloadPhase, final Runnable reloadable) {
        reloadableMap.computeIfAbsent(reloadPhase, phase -> new HashSet<>()).add(reloadable);
    }

    @Override
    public void reload() {
        for (final ReloadPhase phase : ReloadPhase.values()) {
            reloadableMap.getOrDefault(phase, Collections.emptyList()).forEach(Runnable::run);
        }
    }
}
