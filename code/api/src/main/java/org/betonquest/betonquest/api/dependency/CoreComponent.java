package org.betonquest.betonquest.api.dependency;

/**
 * A core component of the BetonQuest plugin representing a unique unit of functionality that may be loaded
 * by a {@link CoreComponentLoader} respecting its dependencies and thereby being loaded in order.
 * <br> <br>
 * Every {@link CoreComponent} defines a list of dependencies in {@link #requires()} that must be injected
 * via {@link #inject(LoadedDependency)} before the component can be loaded.
 * <br> <br>
 * The loading process essentially follows a few steps:
 * <ul>
 *     <li>The {@link #loadComponent(DependencyProvider)} initiates the loading process expecting all dependencies to be
 *     injected and may throw an exception if an invalid state occurs.</li>
 *     <li>During loading the component may choose to provide instances other components depend on via the
 *     {@link DependencyProvider} provided as parameter.</li>
 *     <li>After the loading process has finished successfully, {@link #isLoaded()} returns true</li>
 * </ul>
 *
 * @since 3.0.0
 */
public interface CoreComponent extends DependencyGraphNode {

    /**
     * Injects a loaded dependency into this component and removes it from the list of required dependencies.
     * Injecting a dependency that is not required will have no effect.
     *
     * @param loadedDependency the dependency instance to inject
     * @since 3.0.0
     */
    void inject(LoadedDependency<?> loadedDependency);

    /**
     * Checks whether this component has been loaded.
     * <br> <br>
     * This method must return true after #loadComponent() has been called successfully.
     *
     * @return if this component has been loaded
     * @since 3.0.0
     */
    boolean isLoaded();

    /**
     * Loads this component.
     * <br> <br>
     * A successful loading process should result in {@link #isLoaded()} returning true.
     * A failed loading process should result in {@link #isLoaded()} still returning false and throwing an exception.
     * <br> <br>
     * The loaded dependencies shall be provided using the specified dependency provider
     * to distribute them to all components.
     *
     * @param dependencyProvider the dependency provider to use for loading dependencies
     * @throws IllegalStateException if an invalid state occurs during loading
     * @since 3.0.0
     */
    void loadComponent(DependencyProvider dependencyProvider);
}
