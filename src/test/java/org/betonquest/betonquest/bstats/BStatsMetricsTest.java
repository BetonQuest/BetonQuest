package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link BStatsMetrics} class.
 */
@ExtendWith(BetonQuestLoggerService.class)
class BStatsMetricsTest {

    /**
     * Package path for the test package.
     */
    public static final String PACKAGE_PATH = "test";

    /**
     * Test instruction type and string.
     */
    public static final String TEST_INSTRUCTION = "test";

    /**
     * Another test instruction type and string.
     */
    public static final String OTHER_INSTRUCTION = "other";

    /**
     * Mocked Minecraft Bukkit server.
     */
    private static Server server;

    /**
     * Logger used by the mock Bukkit server instance.
     */
    private static Logger logger;

    /**
     * Create the {@link BStatsMetrics}' test class.
     */
    public BStatsMetricsTest() {

    }

    @BeforeAll
    static void initializeBukkit() {
        logger = mock(Logger.class);
        server = mock(Server.class);
        when(server.getLogger()).thenReturn(logger);
    }

    private void setupLogger() {
        final Plugin plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(logger);

        final PluginManager pluginManager = mock(PluginManager.class);
        when(pluginManager.getPlugins()).thenReturn(new Plugin[]{plugin});
        when(server.getPluginManager()).thenReturn(pluginManager);
    }

    private QuestPackage setupQuestPackage(final BetonQuestLogger logger, final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(), PACKAGE_PATH, packageConfigFile, Collections.emptyList());
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testCreateBStatsMetrics() {
        final Plugin plugin = mock(Plugin.class);

        final PluginDescriptionFile description = new PluginDescriptionFile("BetonQuest", "2.0.0", "org.betonquest.betonquest.BetonQuest");
        when(plugin.getDescription()).thenReturn(description);

        when(plugin.getServer()).thenReturn(server);
        when(server.getBukkitVersion()).thenReturn("1.18.2-R0.1");

        final Metrics metrics = mock(Metrics.class);
        new BStatsMetrics(plugin, metrics, Collections.emptyMap());
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testCustomChartCallableGetsUpdates(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final Plugin plugin = mock(Plugin.class);

        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final PluginDescriptionFile description = new PluginDescriptionFile("BetonQuest", "2.0.0", "org.betonquest.betonquest.BetonQuest");
        when(plugin.getDescription()).thenReturn(description);

        when(plugin.getServer()).thenReturn(server);
        when(server.getBukkitVersion()).thenReturn("1.18.2-R0.1");

        final Metrics bstatsMetrics = mock(Metrics.class);
        final ArgumentCaptor<CustomChart> chartArgumentCaptor = ArgumentCaptor.forClass(CustomChart.class);

        final Map<ID, Void> ids = new HashMap<>();

        final ID firstId = mock(ID.class);
        final Instruction firstInstruction = new Instruction(logger, questPackage, firstId, TEST_INSTRUCTION);
        when(firstId.getInstruction()).thenReturn(firstInstruction);

        ids.put(firstId, null);
        final Map<String, Void> types = new HashMap<>();
        types.put(TEST_INSTRUCTION, null);

        final InstructionMetricsSupplier<ID> metricsSupplier = new CompositeInstructionMetricsSupplier<>(ids::keySet, types::keySet);

        new BStatsMetrics(plugin, bstatsMetrics, Map.of("id", metricsSupplier));

        verify(bstatsMetrics, times(6)).addCustomChart(chartArgumentCaptor.capture());
        final List<CustomChart> customCharts = chartArgumentCaptor.getAllValues();
        final CustomChart countChart = customCharts.get(2);
        final CustomChart enabledChart = customCharts.get(3);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":1}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        types.put(OTHER_INSTRUCTION, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":1}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        final ID secondId = mock(ID.class);
        final Instruction secondInstruction = new Instruction(logger, questPackage, secondId, TEST_INSTRUCTION);
        when(secondId.getInstruction()).thenReturn(secondInstruction);
        ids.put(secondId, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":2}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        final ID thirdId = mock(ID.class);
        final Instruction thirdInstruction = new Instruction(logger, questPackage, thirdId, OTHER_INSTRUCTION);
        when(thirdId.getInstruction()).thenReturn(thirdInstruction);
        ids.put(thirdId, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"other\":1,\"test\":2}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"other\":1,\"test\":1}}}", enabledChart);
    }

    private void assertCollectedChartData(final String expected, final CustomChart chart) {
        assertEquals(expected, chart.getRequestJsonObject((s, throwable) -> fail("Encountered error: " + s), true).toString(), "generated chart data did not match");
    }
}
