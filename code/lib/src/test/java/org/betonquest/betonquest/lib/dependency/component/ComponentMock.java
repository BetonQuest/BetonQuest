package org.betonquest.betonquest.lib.dependency.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Dummy component for testing purposes.
 */
public class ComponentMock extends AbstractCoreComponent {

    /**
     * The dependencies of this component.
     */
    private final Set<Class<?>> dependencies;

    /**
     * The provided classes.
     */
    private final Set<Class<?>> provided;

    /**
     * The method to call when loading this component.
     */
    private final Consumer<DependencyProvider> loadMethod;

    /**
     * Wether this component should inject itself into the dependency provider.
     */
    private final boolean injectSelf;

    /**
     * Create a new dummy component.
     *
     * @param dependencies the dependencies of this component
     */
    public ComponentMock(final Class<?>... dependencies) {
        this(false, dependencies);
    }

    /**
     * Create a new dummy component.
     *
     * @param injectSelf   whether this component should inject itself into the dependency provider
     * @param dependencies the dependencies of this component
     */
    public ComponentMock(final boolean injectSelf, final Class<?>... dependencies) {
        super();
        this.dependencies = Set.of(dependencies);
        this.injectSelf = injectSelf;
        this.provided = injectSelf ? Set.of(ComponentMock.class) : Set.of();
        this.loadMethod = provider -> {
        };
    }

    /**
     * Create a new dummy component.
     *
     * @param loadMethod   the method to call when loading this component
     * @param provided     the provided classes
     * @param dependencies the dependencies of this component
     */
    public ComponentMock(final Consumer<DependencyProvider> loadMethod, final Set<Class<?>> provided, final Class<?>... dependencies) {
        super();
        this.provided = provided;
        this.dependencies = Set.of(dependencies);
        this.injectSelf = false;
        this.loadMethod = loadMethod;
    }

    @Override
    public Set<Class<?>> requires() {
        return dependencies;
    }

    @Override
    public Set<Class<?>> provides() {
        return provided;
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        loadMethod.accept(dependencyProvider);
        if (injectSelf) {
            dependencyProvider.take(ComponentMock.class, this);
        }
    }
}
