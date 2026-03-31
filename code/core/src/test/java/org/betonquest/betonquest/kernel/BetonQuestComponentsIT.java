package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.api.dependency.LoadedDependency;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.integration.IntegrationManager;
import org.betonquest.betonquest.kernel.component.QuestPackageManagerComponent;
import org.betonquest.betonquest.lib.dependency.DefaultLoadedDependency;
import org.betonquest.betonquest.lib.dependency.DependencyHelper;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetonQuestComponentsIT {

    private Set<CoreComponent> components;

    @BeforeEach
    void setUp() {
        this.components = BetonQuestComponents.createDefaults(new File("test.jar"));
    }

    @Test
    void validate_all_components_are_loaded() {
        assertEquals(57, components.size(), "All known components should be loaded");
    }

    @Nested
    class Loaded {

        private Set<LoadedDependency<?>> loadedDependencies;

        private <T> DefaultLoadedDependency<T> mockCreate(final Class<T> type) {
            return new DefaultLoadedDependency<>(type, mock(type));
        }

        @BeforeEach
        void setup() {
            final Set<Class<?>> preinjectedClasses = Set.of(
                    JavaPlugin.class, Server.class, PluginManager.class, BukkitScheduler.class, PluginDescriptionFile.class,
                    ServicesManager.class, BetonQuestLoggerFactory.class, IntegrationManager.class);
            loadedDependencies = preinjectedClasses.stream().map(this::mockCreate).collect(Collectors.toSet());
        }

        @Test
        void invalidate_blocking_dependencies() {
            final Set<CoreComponent> modifiedComponents = BetonQuestComponents.createDefaults(new File("test.jar"));
            modifiedComponents.removeIf(comp -> comp instanceof QuestPackageManagerComponent);
            assertThrows(IllegalStateException.class, () -> DependencyHelper.topologicalOrder(modifiedComponents, loadedDependencies), "Missing dependencies should be detected");
        }

        @Test
        void validate_no_dependencies_are_blocking() {
            assertDoesNotThrow(() -> DependencyHelper.topologicalOrder(components, loadedDependencies), "All components should ordered topologically without any dependencies blocking");
            assertEquals(components.size(), DependencyHelper.topologicalOrder(components, loadedDependencies).size(), "All components should be returned");
        }
    }
}
