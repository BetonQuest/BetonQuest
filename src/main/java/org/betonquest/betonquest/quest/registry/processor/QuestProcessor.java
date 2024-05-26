package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.ID;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the logic around {@link T}.
 *
 * @param <T> the quest type being processed
 * @param <I> the {@link ID} identifying {@link T}
 */
public abstract class QuestProcessor<T, I extends ID> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Loaded {@link T} identified by their {@link ID}.
     */
    protected final Map<I, T> values;

    /**
     * Create a new QuestProcessor to store and execute {@link T} logic.
     *
     * @param log the custom logger for this class
     */
    public QuestProcessor(final BetonQuestLogger log) {
        this.log = log;
        this.values = new HashMap<>();
    }

    /**
     * Clears the values. Used before reloading all QuestPackages.
     */
    public void clear() {
        values.clear();
    }

    /**
     * Gets the number of loaded {@link T}.
     *
     * @return the loaded amount
     */
    public int size() {
        return values.size();
    }

    /**
     * Load all {@link T} from the QuestPackage.
     * <p>
     * Any errors will be logged.
     *
     * @param pack to load the conditions from
     */
    public abstract void load(QuestPackage pack);
}
