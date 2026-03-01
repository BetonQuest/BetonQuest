package org.betonquest.betonquest.lib.dependency.component;

import com.google.common.collect.Sets;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequirementComponentWrapperTest {

    private static final List<Class<?>> RANDOM_CLASSES_TO_PICK = new ArrayList<>(List.of(Actions.class,
            Conditions.class, Objectives.class, Placeholders.class, ArgumentParsers.class,
            Instructions.class));

    private DefaultCoreComponentLoader loader;

    private org.betonquest.betonquest.api.dependency.CoreComponent dummyComponent;

    @Mock
    private BetonQuestLogger logger;

    private static Stream<Arguments> requirementOptions() {
        return Sets.powerSet(new HashSet<>(RANDOM_CLASSES_TO_PICK)).stream().filter(set -> !set.isEmpty()).map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        loader = spy(new DefaultCoreComponentLoader(logger));
        dummyComponent = spy(new ComponentMock(false));
    }

    @Test
    void ensure_called_only_once() {
        final RequirementComponentWrapper wrapped = spy(new RequirementComponentWrapper(dummyComponent, RequirementComponentWrapper.class));
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        loader.load();
        verify(dummyComponent, times(1)).loadComponent(any());
        verify(wrapped, times(1)).loadComponent(any());
    }

    @Test
    void ensure_dependencyProvider_is_only_called_once() {
        final ComponentMock rawDummyComponent = spy(new ComponentMock(provider -> provider.take(String.class, ""), Set.of(String.class)));
        final RequirementComponentWrapper wrapped = spy(new RequirementComponentWrapper(rawDummyComponent, RequirementComponentWrapper.class));
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        loader.load();
        verify(rawDummyComponent, atLeastOnce()).inject(any());
        verify(wrapped, times(1)).loadComponent(any());
    }

    @Test
    void normal_without_Wrapper() {
        loader.register(dummyComponent);
        loader.load();
        assertTrue(dummyComponent.isLoaded(), "Component should be loaded");
    }

    @Test
    void normal_with_wrapper_success_to_verify_component() {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, RequirementComponentWrapper.class);
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        assertFalse(dummyComponent.isLoaded(), "Component should not be loaded");
        loader.load();
        assertTrue(dummyComponent.isLoaded(), "Component should be loaded");
    }

    @Test
    void normal_with_wrapper_success_to_verify_wrapper() {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, RequirementComponentWrapper.class);
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        assertFalse(wrapped.isLoaded(), "Wrapper should not be loaded");
        loader.load();
        assertTrue(wrapped.isLoaded(), "Wrapper should be loaded");
    }

    @Test
    void cannot_load_after_being_loaded() {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, RequirementComponentWrapper.class);
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        assertFalse(dummyComponent.isLoaded(), "Component should not be loaded");
        loader.load();
        assertTrue(dummyComponent.isLoaded(), "Component should be loaded");
    }

    @ParameterizedTest
    @MethodSource("requirementOptions")
    void normal_with_wrapper_fail(final Collection<Class<?>> requirementClasses) {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, requirementClasses.toArray(new Class<?>[0]));
        loader.register(wrapped);
        assertThrows(IllegalStateException.class, loader::load);
        assertFalse(dummyComponent.isLoaded(), "Component should not be loaded");
    }

    @ParameterizedTest
    @MethodSource("requirementOptions")
    void check_for_requirements(final Collection<Class<?>> requirementClasses) {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, requirementClasses.toArray(new Class<?>[0]));
        assertTrue(requirementClasses.stream().allMatch(requirement -> DependencyHelper.isRequired(wrapped.requires(), requirement)), "Wrapped component should require all requirement classes");
    }
}
