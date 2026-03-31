package org.betonquest.betonquest.lib.dependency;

import org.betonquest.betonquest.api.dependency.DependencyGraphNode;
import org.betonquest.betonquest.api.dependency.LoadedDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helps with handling all kinds of dependency-related filtration and analysis.
 */
public final class DependencyHelper {

    private DependencyHelper() {
    }

    /**
     * Filters the remaining dependencies from the given required and loaded dependencies.
     *
     * @param required the required dependencies
     * @param loaded   the loaded dependencies
     * @return the remaining dependencies
     */
    /* default */
    public static Set<Class<?>> remainingDependencies(final Collection<Class<?>> required, final Collection<LoadedDependency<?>> loaded) {
        return required.stream()
                .filter(requirement -> loaded.stream().noneMatch(dependency -> dependency.match(requirement)))
                .collect(Collectors.toSet());
    }

    /**
     * Filters the remaining dependency classes from the given required and loaded classes.
     *
     * @param required the required classes
     * @param loaded   the loaded classes
     * @return the remaining dependency classes
     */
    public static Set<Class<?>> remainingDependencyClasses(final Collection<Class<?>> required, final Collection<Class<?>> loaded) {
        return required.stream()
                .filter(requirement -> loaded.stream().noneMatch(requirement::isAssignableFrom))
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the given dependency is required by the given required dependencies.
     * An instance is required if it is assignable to any of the required dependencies.
     *
     * @param requiredDependencies the required dependencies
     * @param instanceType         the type of the instance to check for requiredness
     * @return true if the instance is required, false otherwise
     */
    /* default */
    public static boolean isRequired(final Collection<Class<?>> requiredDependencies, final Class<?> instanceType) {
        return requiredDependencies.stream().anyMatch(requirement -> requirement.isAssignableFrom(instanceType));
    }

    /**
     * Checks if the given dependency is still required by the given required dependencies if all loaded dependencies
     * are factored out.
     *
     * @param requiredDependencies the required dependencies
     * @param loadedDependencies   the loaded dependencies
     * @param instanceType         the type of the instance to check for requiredness
     * @return true if the instance is still required, false otherwise
     */
    /* default */
    public static boolean isStillRequired(final Collection<Class<?>> requiredDependencies,
                                          final Collection<LoadedDependency<?>> loadedDependencies, final Class<?> instanceType) {
        return isRequired(remainingDependencies(requiredDependencies, loadedDependencies), instanceType);
    }

    /**
     * Orders the given nodes topologically. <br>
     * The resulting list is ordered in such a way that the dependencies of a node are always before it in the list and
     * therefore the list may be loaded in order in a single iteration. <br>
     * In the case of cylic dependencies, the cycle is isolated and an {@link IllegalStateException}
     * with the resolved cycle is thrown.
     *
     * @param nodes              the nodes to order
     * @param loadedDependencies the loaded dependencies
     * @param <T>                the subtype of {@link DependencyGraphNode} of the nodes
     * @return the ordered nodes
     * @throws IllegalStateException if a cyclic dependency is detected
     * @see DependencyGraph
     */
    public static <T extends DependencyGraphNode> List<T> topologicalOrder(final Collection<T> nodes, final Collection<LoadedDependency<?>> loadedDependencies) {
        final List<T> orderedNodes = new ArrayList<>(nodes.size());
        final DependencyGraph<T> graph = buildDependencyGraph(nodes, loadedDependencies);
        final List<T> queue = graph.getNodesWithZeroInDegree();
        final Set<Class<?>> currentlyLoadedDependencies = loadedDependencies.stream().map(LoadedDependency::type).collect(Collectors.toSet());

        while (!queue.isEmpty()) {
            final T current = queue.remove(0);
            final Set<Class<?>> missingDependencies = remainingDependencyClasses(current.requires(), currentlyLoadedDependencies);
            if (!missingDependencies.isEmpty()) {
                throw new IllegalStateException("Node '%s' is missing dependencies: [%s]"
                        .formatted(current.getClass().getSimpleName(), missingDependencies.stream().map(Class::getSimpleName).collect(Collectors.joining(", "))));
            }
            orderedNodes.add(current);
            currentlyLoadedDependencies.addAll(current.provides());
            for (final T dependent : Objects.requireNonNull(graph.dependents().get(current))) {
                graph.decrementInDegree(dependent);
                if (Objects.requireNonNull(graph.inDegree().get(dependent)) == 0) {
                    queue.add(dependent);
                }
            }
        }
        if (orderedNodes.size() != nodes.size()) {
            final List<T> cycleNodes = graph.getNodesWithPositiveInDegree();
            final String cycleDescription = cycleNodes.stream()
                    .map(node -> node.getClass().getSimpleName())
                    .collect(Collectors.joining(" -> "));
            throw new IllegalStateException("Cyclic dependency detected among nodes: " + cycleDescription);
        }
        return orderedNodes;
    }

    /**
     * Builds a dependency graph for the given nodes considering the given loaded dependencies.
     *
     * @param nodes              the nodes to build the graph for
     * @param loadedDependencies the loaded dependencies to resolve in the dependencies of the nodes
     * @param <T>                the subtype of {@link DependencyGraphNode} of the nodes
     * @return the dependency graph
     */
    public static <T extends DependencyGraphNode> DependencyGraph<T> buildDependencyGraph(final Collection<T> nodes, final Collection<LoadedDependency<?>> loadedDependencies) {
        final Map<T, Integer> inDegree = new HashMap<>();
        final Map<T, Set<T>> dependents = new HashMap<>();
        for (final T node : nodes) {
            inDegree.put(node, 0);
            dependents.put(node, new HashSet<>());
        }
        for (final T node : nodes) {
            final Set<Class<?>> remainingRequirements = remainingDependencies(node.requires(), loadedDependencies);
            for (final T potentialDependency : nodes) {
                if (node.equals(potentialDependency)) {
                    continue;
                }
                for (final Class<?> providedType : potentialDependency.provides()) {
                    if (remainingRequirements.stream().anyMatch(req -> req.isAssignableFrom(providedType))) {
                        Objects.requireNonNull(dependents.get(potentialDependency)).add(node);
                        inDegree.put(node, Objects.requireNonNull(inDegree.get(node)) + 1);
                        break;
                    }
                }
            }
        }
        return new DependencyGraph<>(inDegree, dependents);
    }
}
