package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the logic around {@link T}.
 *
 * @param <I> the {@link Identifier} identifying {@link T}
 * @param <T> the quest type being processed
 */
public abstract class QuestProcessor<I extends Identifier, T> {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * The identifier factory to create {@link Identifier}s for this type.
     */
    protected final IdentifierFactory<I> identifierFactory;

    /**
     * Loaded {@link T} identified by their {@link Identifier}.
     */
    protected final Map<I, T> values;

    /**
     * Type name used for logging.
     */
    protected final String readable;

    /**
     * Section name.
     */
    protected final String internal;

    /**
     * Create a new QuestProcessor to store and execute {@link T} logic.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory to create {@link Identifier}s for this type
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     */
    public QuestProcessor(final BetonQuestLogger log,
                          final IdentifierFactory<I> identifierFactory, final String readable, final String internal) {
        this.log = log;
        this.identifierFactory = identifierFactory;
        this.values = new HashMap<>();
        this.readable = readable;
        this.internal = internal;
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
     * Gets a stored {@link T}.
     *
     * @param identifier the id
     * @return the loaded {@link T}
     * @throws QuestException if nothing is stored for the ID
     */
    public T get(final I identifier) throws QuestException {
        final T object = values.get(identifier);
        if (object == null) {
            throw new QuestException("No " + readable + " loaded for ID '" + identifier + "'! Check for errors on /bq reload!");
        }
        return object;
    }

    /**
     * Load all {@link T} from the QuestPackage.
     * <p>
     * Any errors will be logged.
     *
     * @param pack to load the {@link T} from
     */
    public abstract void load(QuestPackage pack);

    /**
     * Creates a new type ID to store the created {@link T} with it.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @return the new typed ID
     * @throws QuestException if the instruction of the identifier could not be created or
     *                        if the ID could not be parsed
     */
    protected I getIdentifier(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        return identifierFactory.parseIdentifier(pack, identifier);
    }

    /**
     * Gets the amount of current loaded {@link T} with their readable name.
     *
     * @return the value size with the identifier
     */
    public String readableSize() {
        return size() + " " + readable;
    }
}
