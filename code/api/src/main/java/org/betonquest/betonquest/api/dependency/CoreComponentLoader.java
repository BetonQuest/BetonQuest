package org.betonquest.betonquest.api.dependency;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * The core component loader essentially manages a number of {@link CoreComponent}s and loads them in the order
 * their dependencies suggest. It detects unsolvable dependencies and throws an exception in case of any.
 */
public interface CoreComponentLoader {

    /**
     * Registers a new core component to be loaded in {@link #load()} later on.
     * The registered component may not be yet loaded.
     *
     * @param component the component to register
     * @throws IllegalArgumentException if the component is already loaded
     */
    void register(CoreComponent component);

    /**
     * Registers a new instance of a dependency that was loaded before this component loader.
     * It will be initially injected into the components that require it before they are loaded.
     *
     * @param type     the class of the dependency
     * @param instance the instance of the dependency
     * @param <T>      the type of the dependency
     * @throws IllegalStateException if the dependency or a subclass of it was already injected
     */
    <T> void init(Class<T> type, T instance);

    /**
     * Get a loaded instance by its type.
     * Will ignore multiple instances of the same type and just return the first one to find.
     *
     * @param type the type of the instance to get
     * @param <T>  the type of the instance
     * @return the loaded instance
     * @throws NoSuchElementException if no instance of the given type was found
     */
    <T> T get(Class<T> type);

    /**
     * Get a loaded instance by its type wrapped in an {@link Optional}.
     * Will ignore multiple instances of the same type and just return the first one to find.
     * <br>
     * Won't throw an exception if no instance of the given type was found but will instead return an empty optional.
     *
     * @param type the type of the instance to get
     * @param <T>  the type of the instance
     * @return the loaded instance wrapped in an optional or an empty optional if no instance was found
     */
    <T> Optional<T> getOptional(Class<T> type);

    /**
     * Get all loaded instances matching a given type.
     *
     * @param type the type of the instances to get
     * @param <T>  the type of the instances
     * @return a collection of loaded instances that may be empty
     */
    <T> Collection<T> getAll(Class<T> type);

    /**
     * Loads all registered components in the correct order as their dependencies suggest.
     * May throw an exception in case of unsolvable dependencies causing the loading to abort.
     *
     * @throws IllegalStateException in case of any errors
     */
    void load();
}
