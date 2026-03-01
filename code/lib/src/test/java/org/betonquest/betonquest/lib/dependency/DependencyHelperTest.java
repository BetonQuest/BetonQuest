package org.betonquest.betonquest.lib.dependency;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.api.dependency.DependencyGraphNode;
import org.betonquest.betonquest.api.dependency.LoadedDependency;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DependencyHelperTest {

    private static final List<Class<?>> RANDOM_CLASSES_TO_PICK = new ArrayList<>(List.of(Actions.class,
            Conditions.class, Objectives.class, Placeholders.class, ArgumentParsers.class,
            Instructions.class));

    private static final List<LoadedDependency<?>> LOADED_DEPENDENCIES = new ArrayList<>(RANDOM_CLASSES_TO_PICK.stream().<LoadedDependency<?>>map(DependencyHelperTest::dep).toList());

    private static <T> LoadedDependency<T> dep(final Class<T> clazz) {
        return new DefaultLoadedDependency<>(clazz, mock(clazz));
    }

    private static DependencyGraphNode node(final Set<Class<?>> requirements, final Set<Class<?>> provided) {
        final DependencyGraphNode mock = mock(DependencyGraphNode.class);
        lenient().when(mock.requires()).thenReturn(requirements);
        lenient().when(mock.provides()).thenReturn(provided);
        return mock;
    }

    private static List<Arguments> nodeCombinations(final boolean valid) {
        final List<Arguments> arguments = new ArrayList<>();
        for (int loadedSubList = 1; loadedSubList < LOADED_DEPENDENCIES.size() - 2; loadedSubList++) {
            Sets.combinations(new HashSet<>(LOADED_DEPENDENCIES), loadedSubList).forEach(loadedDependencies -> {
                final List<Class<?>> remainingClasses = RANDOM_CLASSES_TO_PICK.stream()
                        .filter(clazz -> loadedDependencies.stream().noneMatch(loaded -> loaded.type().equals(clazz))).toList();
                final List<DependencyGraphNode> nodes = IntStream.range(0, remainingClasses.size())
                        .boxed().map(i -> node(i + 1 == remainingClasses.size()
                                ? (valid ? Set.of() : Set.of(remainingClasses.get(0)))
                                : Set.of(remainingClasses.get(i + 1)), Set.of(remainingClasses.get(i)))).toList();
                Collections2.permutations(nodes).forEach(nodesPermutation -> arguments.add(Arguments.of(nodesPermutation, loadedDependencies)));
            });
        }
        return arguments;
    }

    private static Stream<Arguments> validNodeCombinations() {
        return nodeCombinations(true).stream();
    }

    private static Stream<Arguments> invalidNodeCombinations() {
        return nodeCombinations(false).stream();
    }

    private static Stream<Arguments> requirementLoadedCombinations() {
        final List<Arguments> arguments = new ArrayList<>();
        Sets.powerSet(new HashSet<>(RANDOM_CLASSES_TO_PICK)).forEach(subSet -> {
            Sets.powerSet(new HashSet<>(LOADED_DEPENDENCIES)).forEach(loadedSubSet -> {
                arguments.add(Arguments.of(subSet, loadedSubSet));
            });
        });
        return arguments.stream();
    }

    @Test
    void find_simple_topological_order() {
        final List<DependencyGraphNode> nodes = List.of(
                node(Set.of(Placeholders.class, Conditions.class), Set.of(Objectives.class)),
                node(Set.of(), Set.of(Actions.class)),
                node(Set.of(Actions.class), Set.of(Conditions.class)),
                node(Set.of(Actions.class), Set.of(Placeholders.class))
        );
        final List<DependencyGraphNode> dependencyGraphNodes = DependencyHelper.topologicalOrder(nodes, List.of());
        assertTrue(dependencyGraphNodes.get(0).provides().contains(Actions.class), "ActionsComponent should be first");
        assertTrue(dependencyGraphNodes.get(3).provides().contains(Objectives.class), "ObjectivesComponent should be last");
    }

    @Test
    void find_simple_cycle() {
        final List<DependencyGraphNode> nodes = List.of(
                node(Set.of(Placeholders.class, Conditions.class), Set.of(Objectives.class)),
                node(Set.of(), Set.of(Actions.class)),
                node(Set.of(Actions.class), Set.of(Conditions.class)),
                node(Set.of(Actions.class, Instructions.class), Set.of(Placeholders.class)),
                node(Set.of(Objectives.class), Set.of(Instructions.class))
        );
        assertThrows(IllegalStateException.class, () -> DependencyHelper.topologicalOrder(nodes, List.of()), "Should throw an exception because a node has a cyclic dependency");
    }

    @Test
    void simple_blocking_node() {
        final DependencyGraphNode node = node(Set.of(Actions.class), Set.of(Conditions.class));
        assertThrows(IllegalStateException.class, () -> DependencyHelper.topologicalOrder(List.of(node), List.of()), "Should throw an exception because a node is blocking");
    }

    @ParameterizedTest
    @MethodSource("validNodeCombinations")
    void find_correct_loadable_topological_order_for_nodes(final Collection<DependencyGraphNode> nodes, final Collection<LoadedDependency<?>> loadedDependencies) {
        final List<DependencyGraphNode> dependencyGraphNodes = DependencyHelper.topologicalOrder(nodes, loadedDependencies);
        final List<Class<?>> loaded = new ArrayList<>(loadedDependencies.stream().map(LoadedDependency::type).toList());
        for (final DependencyGraphNode node : dependencyGraphNodes) {
            if (!loaded.containsAll(node.requires())) {
                fail("Node %s requires %s which is not loaded".formatted(node.provides(), node.requires()));
            }
            loaded.addAll(node.provides());
        }
    }

    @ParameterizedTest
    @MethodSource("invalidNodeCombinations")
    void find_cyclic_dependency_for_nodes(final Collection<DependencyGraphNode> nodes, final Collection<LoadedDependency<?>> loadedDependencies) {
        assertThrows(IllegalStateException.class, () -> DependencyHelper.topologicalOrder(nodes, loadedDependencies), "Should throw an exception because a node has a cyclic dependency");
    }

    @ParameterizedTest
    @MethodSource("validNodeCombinations")
    void retain_all_nodes(final Collection<DependencyGraphNode> nodes, final Collection<LoadedDependency<?>> loadedDependencies) {
        final List<DependencyGraphNode> dependencyGraphNodes = DependencyHelper.topologicalOrder(nodes, loadedDependencies);
        assertEquals(nodes.size(), dependencyGraphNodes.size(), "Nodes should be retained");
        assertTrue(dependencyGraphNodes.containsAll(nodes), "Nodes should be retained");
    }

    @ParameterizedTest
    @MethodSource("requirementLoadedCombinations")
    void remaining_requirements_are_disjoint_from_loaded_dependency(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        final boolean classesDisjointFromLoaded = Collections.disjoint(classes, loadedDependencies.stream().map(LoadedDependency::type).collect(Collectors.toSet()));
        assertTrue(classesDisjointFromLoaded, "Remaining requirements should be disjoint from loaded: %s vs. %s"
                .formatted(classes.stream().map(Class::getSimpleName).toList(), loadedDependencies.stream().map(LoadedDependency::type).map(Class::getSimpleName).toList()));
    }

    @ParameterizedTest
    @MethodSource("requirementLoadedCombinations")
    void remaining_classes_are_still_required(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        assertTrue(classes.stream().allMatch(cl -> DependencyHelper.isStillRequired(requirements, loadedDependencies, cl)), "Class should be still required");
    }

    @ParameterizedTest
    @MethodSource("requirementLoadedCombinations")
    void loaded_dependency_are_not_required(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
        final boolean anyLoadedStillRequired = loadedDependencies.stream().map(LoadedDependency::type)
                .anyMatch(loaded -> DependencyHelper.isStillRequired(requirements, loadedDependencies, loaded));
        assertFalse(anyLoadedStillRequired, "Class should not be required");
    }

    @Test
    void subclasses_of_dependencies_are_also_required() {
        assertTrue(DependencyHelper.isRequired(List.of(CoreComponent.class), AbstractCoreComponent.class),
                "Subclasses of dependencies should be required");
        assertFalse(DependencyHelper.isRequired(List.of(AbstractCoreComponent.class), CoreComponent.class),
                "Superclasses of dependencies should not be required");
    }

    @Test
    void subclasses_of_dependencies_are_not_required_if_loaded() {
        assertTrue(DependencyHelper.isStillRequired(List.of(CoreComponent.class), List.of(), AbstractCoreComponent.class),
                "Subclasses of dependencies should be required if not loaded");
        assertFalse(DependencyHelper.isStillRequired(List.of(CoreComponent.class), List.of(dep(CoreComponent.class)), AbstractCoreComponent.class),
                "Subclasses of dependencies should not be required if the superclass is loaded");
    }

    @Test
    void create_correct_dependency_graph_maps() {
        final List<DependencyGraphNode> nodes = List.of(
                node(Set.of(), Set.of(Actions.class)),
                node(Set.of(Actions.class), Set.of(Conditions.class)),
                node(Set.of(Actions.class), Set.of(Placeholders.class)),
                node(Set.of(Placeholders.class, Conditions.class), Set.of(Objectives.class))
        );
        final DependencyGraph<DependencyGraphNode> dependencyGraph = DependencyHelper.buildDependencyGraph(nodes, Set.of());
        assertEquals(4, dependencyGraph.inDegree().size(), "Indegree should be 4");
        assertEquals(4, dependencyGraph.dependents().size(), "Dependents should be 3");
    }

    @Test
    void create_correct_dependency_graph_in_degrees() {
        final List<DependencyGraphNode> nodes = List.of(
                node(Set.of(), Set.of(Actions.class)),
                node(Set.of(Actions.class), Set.of(Conditions.class)),
                node(Set.of(Actions.class), Set.of(Placeholders.class)),
                node(Set.of(Placeholders.class, Conditions.class), Set.of(Objectives.class))
        );

        final DependencyGraph<DependencyGraphNode> dependencyGraph = DependencyHelper.buildDependencyGraph(nodes, Set.of());
        final Map<DependencyGraphNode, Integer> expectedInDegree = Map.of(nodes.get(0), 0, nodes.get(1), 1, nodes.get(2), 1, nodes.get(3), 2);
        final String actualInDegrees = nodes.stream().map(node -> node.hashCode() + ":" + dependencyGraph.inDegree().get(node)).collect(Collectors.joining(","));
        final String expectedInDegrees = nodes.stream().map(node -> node.hashCode() + ":" + expectedInDegree.get(node)).collect(Collectors.joining(","));
        assertEquals(expectedInDegrees, actualInDegrees, "Indegree should be match");

        final Map<DependencyGraphNode, Set<DependencyGraphNode>> expectedDependents = Map.of(nodes.get(0), Set.of(nodes.get(1), nodes.get(2)), nodes.get(1), Set.of(nodes.get(3)),
                nodes.get(2), Set.of(nodes.get(3)), nodes.get(3), Set.of());
        final String actualDependents = nodes.stream().map(node -> "%d:%s".formatted(node.hashCode(),
                dependencyGraph.dependents().get(node).stream().map(DependencyGraphNode::hashCode).sorted().toList())).collect(Collectors.joining(","));
        final String expectedDependentsString = nodes.stream().map(node -> "%d:%s".formatted(node.hashCode(),
                expectedDependents.get(node).stream().map(DependencyGraphNode::hashCode).sorted().toList())).collect(Collectors.joining(","));
        assertEquals(expectedDependentsString, actualDependents, "Dependents should be match");
    }
}
