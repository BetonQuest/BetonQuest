package org.betonquest.betonquest.lib.dependency.component;

import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.lib.dependency.DefaultLoadedDependency;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractCoreComponentTest {

    private AbstractCoreComponent component;

    @Mock
    private BetonQuestLogger logger;

    private CoreComponentLoader loader;

    @BeforeEach
    void setUp() {
        component = spy(new ComponentMock(BetonQuestLogger.class));
        loader = new DefaultCoreComponentLoader(logger);
    }

    @Test
    void inject_dependency_into_component_to_test_requires_method() {
        assertTrue(DependencyHelper.isStillRequired(component.requires(), component.injectedDependencies, BetonQuestLogger.class), "Component should require BetonQuestLogger");
        component.inject(new DefaultLoadedDependency<>(BetonQuestLogger.class, logger));
        assertFalse(DependencyHelper.isStillRequired(component.requires(), component.injectedDependencies, BetonQuestLogger.class), "Component should not require BetonQuestLogger anymore");
    }

    @Test
    void multiple_injections_are_skipped() {
        for (int i = 0; i < 50; i++) {
            component.inject(new DefaultLoadedDependency<>(BetonQuestLogger.class, logger));
        }
        assertEquals(1, component.injectedDependencies.size(), "Component should only have one dependency");
    }

    @Test
    void loading_with_missing_dependencies_fails() {
        loader.register(component);
        assertThrows(IllegalStateException.class, loader::load, "Component should not be loadable without dependencies");
        verify(component, never()).load(any());
    }

    @Test
    void loading_with_fulfilled_dependencies() {
        loader.init(BetonQuestLogger.class, logger);
        loader.register(component);
        loader.load();
        verify(component, times(1)).load(any());
        assertTrue(component.isLoaded(), "Component should be loaded after loading");
    }

    @Test
    void get_dependency_with_fulfilled_requirement() {
        component.inject(new DefaultLoadedDependency<>(BetonQuestLogger.class, logger));
        assertEquals(logger, component.getDependency(BetonQuestLogger.class), "Should return injected dependency");
    }

    @Test
    void attempt_to_get_missing_dependency_fails() {
        assertThrows(NoSuchElementException.class, () -> component.getDependency(BetonQuestLogger.class), "Should throw exception on missing dependency");
    }

    @Test
    void component_requires_injection() {
        assertTrue(component.requires(BetonQuestLogger.class), "Component should require BetonQuestLogger");
        component.inject(new DefaultLoadedDependency<>(BetonQuestLogger.class, logger));
        assertFalse(component.requires(BetonQuestLogger.class), "Component should no longer require BetonQuestLogger");
    }

    @Test
    void loading_component_propagates_dependency_provider_and_is_loaded_afterwards() {
        component.inject(new DefaultLoadedDependency<>(BetonQuestLogger.class, logger));
        final DependencyProvider mockedProvider = mock(DependencyProvider.class);
        component.loadComponent(mockedProvider);
        assertTrue(component.isLoaded(), "Component should be loaded after loading");
        verify(component, times(1)).load(mockedProvider);
    }
}


