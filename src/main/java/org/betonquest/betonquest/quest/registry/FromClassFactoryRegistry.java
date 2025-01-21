package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Stores features that can be used in BetonQuest.
 *
 * @param <T> the feature to store/create
 * @param <F> the factory to create new {@link T}
 */
public abstract class FromClassFactoryRegistry<T, F> extends FactoryRegistry<F> {
    /**
     * Create a new from class registry.
     *
     * @param log      the logger that will be used for logging
     * @param typeName the name of the type to use in the register log message
     */
    public FromClassFactoryRegistry(final BetonQuestLogger log, final String typeName) {
        super(log, typeName);
    }

    /**
     * Catches exceptions that can occur while creating new instances and wrap them into QuestExceptions.
     *
     * @param typeName    the type name used in the exception message
     * @param constructor the used constructor
     * @param args        the constructor arguments
     * @param <T>         the class to construct
     * @return the newly created object
     * @throws QuestException when the construction fails
     */
    protected static <T> T catchConstructionException(final String typeName, final Constructor<T> constructor, final Object... args) throws QuestException {
        try {
            return constructor.newInstance(args);
        } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
                       | InvocationTargetException | SecurityException exception) {
            throw new QuestException("Couldn't load " + typeName + " '" + constructor.getName() + "': " + exception.getMessage(), exception);
        }
    }

    /**
     * Register a new factory from Class constructor.
     *
     * @param name  name of the schedule type
     * @param clazz the implementation class
     */
    public void register(final String name, final Class<? extends T> clazz) {
        try {
            register(name, createFactory(clazz));
        } catch (final NoSuchMethodException | SecurityException e) {
            log.warn("Cannot register factory '" + name + "' for " + typeName, e);
        }
    }

    /**
     * Create a new Factory for the given class.
     *
     * @param clazz the class to create with the factory
     * @return the factory for the given specific class
     * @throws NoSuchMethodException if the class does not contain the correct constructor
     */
    protected abstract F createFactory(Class<? extends T> clazz) throws NoSuchMethodException;
}
