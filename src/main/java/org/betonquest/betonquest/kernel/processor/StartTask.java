package org.betonquest.betonquest.kernel.processor;

/**
 * Marks a processor to have tasks that needs to run after all Quests are loaded.
 */
@FunctionalInterface
public interface StartTask {

    /**
     * Start all actions of the processor after re-/loading.
     */
    void startAll();
}
