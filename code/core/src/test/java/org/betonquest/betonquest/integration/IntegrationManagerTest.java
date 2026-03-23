package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicesManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerExtension.class)
@ExtendWith(MockitoExtension.class)
class IntegrationManagerTest {

    private static final Version PLUGIN_VERSION = VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "2.0.0");

    private static final Version INVALID_PLUGIN_VERSION = VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "3.0.0");

    private static final String MINECRAFT_VERSION = "1.18.2";

    private static final String INVALID_MINECRAFT_VERSION = "1.20.4";

    private static ServicesManager servicesManager;

    private static MockedStatic<Bukkit> bukkit;

    @Mock
    private BetonQuestLogger logger;

    @Mock
    private BetonQuestLoggerFactory loggerFactory;

    @Mock
    private Plugin integratingPlugin;

    @Mock
    private Integration integration;

    private Supplier<Integration> integrationSupplier;

    @Mock
    private BetonQuestApiService betonQuestApiService;

    @Mock
    private BetonQuestApi betonQuestApi;

    @Mock
    private PluginDescriptionFile descriptionFile;

    private PluginProvider pluginProvider;

    private IntegrationManager integrationManager;

    @BeforeAll
    static void setupOnce() {
        servicesManager = mock(ServicesManager.class);
        final Server server = mock(Server.class);
        lenient().when(server.getMinecraftVersion()).thenReturn(MINECRAFT_VERSION);
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getServicesManager).thenReturn(servicesManager);
        bukkit.when(Bukkit::getServer).thenReturn(server);
        bukkit.when(Bukkit::getName).thenReturn("Test");
        bukkit.when(Bukkit::getMinecraftVersion).thenReturn(MINECRAFT_VERSION);
    }

    @AfterAll
    static void shutdown() {
        bukkit.close();
    }

    @BeforeEach
    void setUp() {
        integrationSupplier = () -> integration;
        integrationManager = new IntegrationManager(logger, loggerFactory);
        lenient().when(loggerFactory.create(any(Class.class))).thenReturn(logger);
        lenient().when(servicesManager.load(BetonQuestApiService.class)).thenReturn(betonQuestApiService);
        lenient().when(betonQuestApiService.api(any())).thenReturn(betonQuestApi);
        lenient().when(integratingPlugin.isEnabled()).thenReturn(true);
        lenient().when(integratingPlugin.getDescription()).thenReturn(descriptionFile);
        lenient().when(descriptionFile.getVersion()).thenReturn(PLUGIN_VERSION.toString());
        pluginProvider = PluginProvider.forInstance(integratingPlugin);
    }

    @Test
    void valid_register_without_enable() throws QuestException {
        lenient().when(integratingPlugin.isEnabled()).thenReturn(false);
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of());
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.requirePlugin(pluginProvider)));
        integrationManager.enable(betonQuestApiService);
        verify(integration, never()).enable(betonQuestApi);
    }

    @Test
    void valid_register_with_automatic_enable_in_enabled_manager_state() throws QuestException {
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of());
        integrationManager.enable(betonQuestApiService);
        verify(integration, times(1)).enable(betonQuestApi);
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.requirePlugin(pluginProvider)));
        verify(integration, times(2)).enable(betonQuestApi);
    }

    @Test
    void valid_register_with_valid_version_should_enable() throws QuestException {
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.minimalPluginVersion(pluginProvider, PLUGIN_VERSION)));
        integrationManager.enable(betonQuestApiService);
        verify(integration, times(1)).enable(betonQuestApi);
    }

    @Test
    void valid_register_with_invalid_version_should_not_enable() throws QuestException {
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.minimalPluginVersion(pluginProvider, INVALID_PLUGIN_VERSION)));
        integrationManager.enable(betonQuestApiService);
        verify(integration, never()).enable(betonQuestApi);
    }

    @Test
    void valid_register_with_valid_minecraft_version_should_enable() throws QuestException {
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.minimalVanillaVersion(MINECRAFT_VERSION)));
        integrationManager.enable(betonQuestApiService);
        verify(integration, times(1)).enable(betonQuestApi);
    }

    @Test
    void valid_register_with_invalid_minecraft_version_should_not_enable() throws QuestException {
        integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.minimalVanillaVersion(INVALID_MINECRAFT_VERSION)));
        integrationManager.enable(betonQuestApiService);
        verify(integration, never()).enable(betonQuestApi);
    }

    @Nested
    class with_basic_registration {

        @BeforeEach
        void setUp() {
            integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.requirePlugin(pluginProvider)));
        }

        @Test
        void failing_to_enable_should_log_warning() throws QuestException {
            doThrow(new QuestException("Could not enable")).when(integration).enable(any());
            assertDoesNotThrow(() -> integrationManager.enable(betonQuestApiService));
            verify(logger, times(1)).warn(anyString(), any(QuestException.class));
        }

        @Test
        void failing_to_enable_with_non_quest_exception_should_log_error() throws QuestException {
            doThrow(new RuntimeException("Could not enable")).when(integration).enable(any());
            assertDoesNotThrow(() -> integrationManager.enable(betonQuestApiService));
            verify(logger, times(1)).error(anyString(), any(RuntimeException.class));
        }

        @Nested
        class after_manager_enabled {

            @BeforeEach
            void setUp() {
                integrationManager.enable(betonQuestApiService);
            }

            @Test
            void failing_to_post_enable_should_log_warning() throws QuestException {
                doThrow(new QuestException("Could not post-enable")).when(integration).postEnable(any());
                assertDoesNotThrow(() -> integrationManager.postEnable(betonQuestApiService));
                verify(logger, times(1)).warn(anyString(), any(QuestException.class));
            }

            @Test
            void failing_to_post_enable_with_non_quest_exception_should_log_error() throws QuestException {
                doThrow(new RuntimeException("Could not post-enable")).when(integration).postEnable(any());
                assertDoesNotThrow(() -> integrationManager.postEnable(betonQuestApiService));
                verify(logger, times(1)).error(anyString(), any(RuntimeException.class));
            }

            @Test
            void valid_register_with_enable() throws QuestException {
                verify(integration, times(1)).enable(betonQuestApi);
            }

            @Test
            void integrations_should_be_enabled_only_once() throws QuestException {
                integrationManager.enable(betonQuestApiService);
                verify(integration, times(1)).enable(betonQuestApi);
            }

            @Nested
            class after_post_enable {

                @BeforeEach
                void setUp() {
                    integrationManager.postEnable(betonQuestApiService);
                }

                @Test
                void failing_to_teardown_should_log_warning() throws QuestException {
                    doThrow(new QuestException("Could not teardown")).when(integration).disable();
                    assertDoesNotThrow(integrationManager::disable);
                    verify(logger, times(1)).warn(anyString(), any(QuestException.class));
                }

                @Test
                void failing_to_teardown_with_non_quest_exception_should_log_error() throws QuestException {
                    doThrow(new RuntimeException("Could not teardown")).when(integration).disable();
                    assertDoesNotThrow(integrationManager::disable);
                    verify(logger, times(1)).error(anyString(), any(RuntimeException.class));
                }

                @Test
                void post_enable_should_call_enable_on_integration() throws QuestException {
                    verify(integration, times(1)).postEnable(betonQuestApi);
                }

                @Test
                void post_enable_should_prevent_new_registrations() {
                    assertThrows(IllegalStateException.class, () -> integrationManager.register(integrationSupplier, integratingPlugin, Set.of(Policies.requirePlugin(pluginProvider))));
                    integrationManager.enable(betonQuestApiService);
                    verify(logger, times(1)).warn(anyString());
                }

                @Test
                void teardown_should_propagate() throws QuestException {
                    integrationManager.disable();
                    verify(integration, times(1)).disable();
                }
            }
        }
    }
}

