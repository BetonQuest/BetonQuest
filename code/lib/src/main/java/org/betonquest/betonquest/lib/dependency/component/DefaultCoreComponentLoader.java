package org.betonquest.betonquest.lib.dependency.component;

import org.apache.commons.lang3.time.StopWatch;
import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.dependency.LoadedDependency;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.lib.dependency.DefaultLoadedDependency;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link CoreComponentLoader}.
 */
public class DefaultCoreComponentLoader implements CoreComponentLoader {

    /**
     * Contains all registered components.
     */
    private final Set<CoreComponent> components;

    /**
     * Contains all initial injections.
     */
    private final Set<LoadedDependency<?>> initialInjections;

    /**
     * Contains all loaded components objects.
     */
    private final Set<LoadedDependency<?>> loaded;

    /**
     * The logger to use for debugging and error messages.
     */
    private final BetonQuestLogger log;

    /**
     * Create a new component loader.
     *
     * @param log the logger to use
     */
    public DefaultCoreComponentLoader(final BetonQuestLogger log) {
        this.log = log;
        this.components = new LinkedHashSet<>();
        this.initialInjections = new LinkedHashSet<>();
        this.loaded = new LinkedHashSet<>();
    }

    @Override
    public void register(final CoreComponent component) {
        if (component.isLoaded()) {
            throw new IllegalArgumentException("Cannot register already loaded component: %s - Use CoreComponentLoader.init() instead.".formatted(component.getClass().getSimpleName()));
        }
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> type, final T instance) {
        final LoadedDependency<T> newlyAddedDependency = new DefaultLoadedDependency<>(type, instance);
        if (initialInjections.stream().anyMatch(dependency -> dependency.match(type) || newlyAddedDependency.match(dependency.type()))) {
            final String existing = initialInjections.stream().filter(dependency -> dependency.match(type))
                    .map(LoadedDependency::type).map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
            throw new IllegalStateException("Attempted to inject an instance for an already injected dependency: new %s ~ %s".formatted(type.getSimpleName(), existing));
        }
        initialInjections.add(newlyAddedDependency);
    }

    @Override
    public <T> T get(final Class<T> type) {
        return getOptional(type).orElseThrow(() -> new NoSuchElementException("No dependency of type %s found".formatted(type.getSimpleName())));
    }

    @Override
    public <T> Optional<T> getOptional(final Class<T> type) {
        return loaded.stream()
                .filter(dependency -> dependency.match(type))
                .map(LoadedDependency::dependency)
                .map(type::cast)
                .findFirst();
    }

    @Override
    public <T> Collection<T> getAll(final Class<T> type) {
        return loaded.stream()
                .filter(dependency -> dependency.match(type))
                .map(LoadedDependency::dependency)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public void load() {
        log.info("Loading %s BetonQuest components...".formatted(components.size()));
        initialInjections.forEach(this::injectToAll);
        log.debug("Injected initial %s dependencies into components.".formatted(initialInjections.size()));
        loadedComponentsWarningCheck();
        if (components.isEmpty()) {
            log.warn("No components were registered. Skipping loading.");
            return;
        }
        final List<CoreComponent> orderedComponents = DependencyHelper.topologicalOrder(components, loaded);
        log.debug("Component order for %s components: %s".formatted(orderedComponents.size(), orderedComponents.stream().map(CoreComponent::getClass).map(Class::getSimpleName).collect(Collectors.joining(","))));
        final StopWatch stopWatch = StopWatch.createStarted();
        orderedComponents.forEach(this::loadComponent);
        stopWatch.stop();
        log.debug("Loaded %s components, took %s".formatted(components.size(), stopWatch.formatTime()));
        log.info("All %s components successfully loaded.".formatted(components.size()));
    }

    private void loadedComponentsWarningCheck() {
        if (components.stream().anyMatch(CoreComponent::isLoaded)) {
            final String loaded = components.stream().filter(CoreComponent::isLoaded)
                    .map(CoreComponent::getClass).map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
            components.removeIf(CoreComponent::isLoaded);
            log.warn("Components '%s' were already loaded. This might lead to unexpected behavior.".formatted(loaded));
        }
    }

    private void loadComponent(final CoreComponent component) {
        final StopWatch stopWatch = StopWatch.createStarted();
        final List<Class<?>> expectedProvidedClasses = new ArrayList<>(component.provides());
        component.loadComponent(createDependencyProvider(component, loadedDependency -> {
            expectedProvidedClasses.removeIf(expectedProvidedClass -> expectedProvidedClass.isAssignableFrom(loadedDependency.type()));
            injectToAll(loadedDependency);
        }));
        stopWatch.stop();
        if (!expectedProvidedClasses.isEmpty()) {
            final String missing = expectedProvidedClasses.stream().map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
            throw new IllegalStateException("Component %s did not provide all expected dependencies, missing: %s".formatted(component.getClass().getSimpleName(), missing));
        }
        log.debug("Loaded component '%s'. Took %s".formatted(component.getClass().getSimpleName(), stopWatch.formatTime()));
    }

    private void injectToAll(final LoadedDependency<?> loadedDependency) {
        components.forEach(component -> component.inject(loadedDependency));
        loaded.add(loadedDependency);
    }

    private DependencyProvider createDependencyProvider(final CoreComponent activeComponent, final Consumer<LoadedDependency<?>> injectionReceiver) {
        return new DependencyProvider() {
            @Override
            public <T> void take(final Class<T> type, final T dependency) {
                if (!activeComponent.provides().contains(type)) {
                    throw new IllegalStateException("Component %s attempted to inject a dependency that is not supposed to be provided by it: %s not in (%s)"
                            .formatted(activeComponent.getClass().getSimpleName(), type.getSimpleName(), activeComponent.provides().stream().map(Class::getSimpleName).collect(Collectors.joining(","))));
                }
                if (loaded.stream().anyMatch(known -> known.type().isAssignableFrom(type) || type.isAssignableFrom(known.type()))) {
                    throw new IllegalStateException("Component %s attempted to load a dependency compatible with another. This might cause non-deterministic behaviour and is therefore prevented: new %s ~ %s"
                            .formatted(activeComponent.getClass().getSimpleName(), type.getSimpleName(), dependency.getClass().getSimpleName()));
                }
                final LoadedDependency<T> loadedDependency = new DefaultLoadedDependency<>(type, dependency);
                injectionReceiver.accept(loadedDependency);
            }
        };
    }
}
