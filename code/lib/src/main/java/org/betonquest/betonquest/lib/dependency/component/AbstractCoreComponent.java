package org.betonquest.betonquest.lib.dependency.component;

import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.dependency.LoadedDependency;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default abstract implementation of {@link CoreComponent} aiming to reduce boilerplate code.
 */
public abstract class AbstractCoreComponent implements CoreComponent {

    /**
     * The injected dependencies of this component.
     */
    protected final Set<LoadedDependency<?>> injectedDependencies;

    /**
     * Whether this component was already loaded.
     */
    private boolean loaded;

    /**
     * Create a new component.
     */
    public AbstractCoreComponent() {
        this.injectedDependencies = new HashSet<>();
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        if (requires(dependency.type())) {
            this.injectedDependencies.add(dependency);
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public final void loadComponent(final DependencyProvider dependencyProvider) {
        if (isLoaded() || !DependencyHelper.remainingDependencies(requires(), injectedDependencies).isEmpty()) {
            throw new IllegalStateException("Cannot load component providing '%s' because it still requires '%s'".formatted(
                    provides().stream().map(Class::getSimpleName).collect(Collectors.joining(",")),
                    DependencyHelper.remainingDependencies(requires(), injectedDependencies).stream().map(Class::getSimpleName).collect(Collectors.joining(","))));
        }
        load(dependencyProvider);
        this.loaded = true;
    }

    /**
     * Get a dependency of this component.
     *
     * @param type the {@link Class} of the dependency to get
     * @param <U>  the type of the dependency
     * @return the dependency
     */
    protected <U> U getDependency(final Class<U> type) {
        final LoadedDependency<?> injectedDependency = injectedDependencies.stream()
                .filter(dependency -> dependency.match(type)).findFirst().orElseThrow();
        return type.cast(injectedDependency.dependency());
    }

    /**
     * Checks whether this component still requires the specified type.
     * May be used to check whether a dependency has been already injected.
     * <br>
     * Generally refers to dependencies defined in {@link #requires()}.
     *
     * @param type the type to check
     * @return if this component still requires the specified type
     */
    protected boolean requires(final Class<?> type) {
        return DependencyHelper.isStillRequired(requires(), injectedDependencies, type);
    }

    @Override
    public abstract Set<Class<?>> requires();

    @Override
    public Set<Class<?>> provides() {
        return Set.of();
    }

    /**
     * Implement all loading logic here and use the provided {@link DependencyProvider} to inject dependencies into
     * all other components.
     *
     * @param dependencyProvider the dependency provider to use
     */
    protected abstract void load(DependencyProvider dependencyProvider);
}
