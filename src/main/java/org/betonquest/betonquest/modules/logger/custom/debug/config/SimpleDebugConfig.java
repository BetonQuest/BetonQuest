package org.betonquest.betonquest.modules.logger.custom.debug.config;


import org.betonquest.betonquest.api.config.ConfigurationFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a simple debug configuration.
 */
public class SimpleDebugConfig implements DebugConfig {
    /**
     * Runnables to execute when debugging is started
     */
    private final Map<Object, PrePostRunnable> onStartHandlers;
    /**
     * Runnables to execute when debugging is stopped
     */
    private final Map<Object, PrePostRunnable> onStopHandlers;
    /**
     * Whether debugging is enabled.
     */
    private final AtomicBoolean debugging;

    /**
     * Create a new {@link DebugConfig} that is based on a {@link ConfigurationFile}.
     *
     * @param initialValue initial state of debugging
     */
    public SimpleDebugConfig(final boolean initialValue) {
        this.onStartHandlers = new HashMap<>();
        this.onStopHandlers = new HashMap<>();
        this.debugging = new AtomicBoolean(initialValue);
    }

    @Override
    public boolean isDebugging() {
        return debugging.get();
    }

    @Override
    public void startDebug() throws IOException {
        setDebug(true, onStartHandlers);
    }

    @Override
    public void stopDebug() throws IOException {
        setDebug(false, onStopHandlers);
    }

    private void setDebug(final boolean newDebugging, final Map<Object, PrePostRunnable> handlers) {
        if (debugging.get() == newDebugging) {
            return;
        }
        for (final PrePostRunnable runnable : handlers.values()) {
            runnable.preRun();
        }
        try {
            if (debugging.compareAndSet(!newDebugging, newDebugging)) {
                for (final Runnable runnable : handlers.values()) {
                    runnable.run();
                }
            }
        } finally {
            for (final PrePostRunnable runnable : handlers.values()) {
                runnable.postRun();
            }
        }
    }

    @Override
    public int getExpireAfterMinutes() {
        return 10;
    }

    @Override
    public void addOnStartHandler(final Object object, final PrePostRunnable onStart) {
        onStartHandlers.put(object, onStart);
    }

    @Override
    public void removeOnStartHandler(final Object object) {
        onStartHandlers.remove(object);
    }

    @Override
    public void addOnStopHandler(final Object object, final PrePostRunnable onStop) {
        onStopHandlers.put(object, onStop);
    }


    @Override
    public void removeOnStopHandler(final Object object) {
        onStopHandlers.remove(object);
    }
}
