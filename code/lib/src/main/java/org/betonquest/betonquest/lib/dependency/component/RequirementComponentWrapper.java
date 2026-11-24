package org.betonquest.betonquest.lib.dependency.component;

import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.dependency.LoadedDependency;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper for {@link CoreComponent} to add additional requirements.
 */
public class RequirementComponentWrapper implements CoreComponent {

    /**
     * The wrapped component.
     */
    private final CoreComponent wrappedComponent;

    /**
     * The additional requirements.
     */
    private final Set<Class<?>> requirements;

    /**
     * The loaded requirements.
     */
    private final Set<LoadedDependency<?>> loadedRequirements;

    /**
     * Create a new wrapper for the specified component with additional requirements.
     *
     * @param wrappedComponent       the wrapped component
     * @param additionalRequirements the additional requirements
     */
    public RequirementComponentWrapper(final CoreComponent wrappedComponent, final Class<?>... additionalRequirements) {
        super();
        this.wrappedComponent = wrappedComponent;
        this.requirements = new HashSet<>();
        this.requirements.addAll(wrappedComponent.requires());
        this.requirements.addAll(Set.of(additionalRequirements));
        this.loadedRequirements = new HashSet<>();
    }

    @Override
    public Set<Class<?>> requires() {
        return requirements;
    }

    @Override
    public Set<Class<?>> provides() {
        return wrappedComponent.provides();
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        if (DependencyHelper.isStillRequired(requirements, loadedRequirements, dependency.type())) {
            loadedRequirements.add(dependency);
            wrappedComponent.inject(dependency);
        }
    }

    @Override
    public boolean isLoaded() {
        return wrappedComponent.isLoaded();
    }

    @Override
    public void loadComponent(final DependencyProvider dependencyProvider) {
        if (isLoaded() || !DependencyHelper.remainingDependencies(requirements, loadedRequirements).isEmpty()) {
            throw new IllegalStateException("Cannot load component '%s' because it still requires '%s'".formatted(wrappedComponent.getClass().getName(),
                    DependencyHelper.remainingDependencies(requirements, loadedRequirements).stream().map(Class::getSimpleName).collect(Collectors.joining(","))));
        }
        wrappedComponent.loadComponent(dependencyProvider);
    }
}
