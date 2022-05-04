package org.betonquest.betonquest.modules.logger.custom.debug.config;

import java.io.IOException;

/**
 * This interface provides methods to manage settings related to debug settings.
 */
public interface DebugConfig {

    /**
     * @return True, if debugging is enabled
     */
    boolean isDebugging();

    /**
     * Starts debugging.
     *
     * @throws IOException If there was an issue start debugging
     */
    void startDebug() throws IOException;

    /**
     * Stops debugging.
     *
     * @throws IOException If there was an issue stop debugging
     */
    void stopDebug() throws IOException;

    /**
     * Gets how long {@link java.util.logging.LogRecord}s stay in a cache.
     * <p>
     * If it is 0, no history will be saved at all.
     *
     * @return duration until expiration in minutes
     */
    int getExpireAfterMinutes();

    /**
     * Adds a runnable to execute when debugging is started.
     *
     * @param object  the object registering this handler
     * @param onStart the runnable
     */
    void addOnStartHandler(final Object object, final PrePostRunnable onStart);

    /**
     * Removes a runnable to execute when debugging is started.
     *
     * @param object the object registered this handler
     */
    void removeOnStartHandler(final Object object);

    /**
     * Adds a runnable to execute when debugging is stopped.
     *
     * @param object the object registering this handler
     * @param onStop the runnable
     */
    void addOnStopHandler(final Object object, final PrePostRunnable onStop);

    /**
     * Removes a runnable to execute when debugging is stopped.
     *
     * @param object the object registered this handler
     */
    void removeOnStopHandler(final Object object);

    /**
     * {@link Runnable} with a method to call before and after teh actual run.
     */
    interface PrePostRunnable extends Runnable {
        /**
         * Executed before the {@link Runnable#run()} method is called.
         */
        void preRun();

        /**
         * Executed after the {@link Runnable#run()} method is called.
         */
        void postRun();
    }
}
