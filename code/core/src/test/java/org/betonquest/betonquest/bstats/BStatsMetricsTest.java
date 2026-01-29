package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
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

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(factory, logger), PACKAGE_PATH, packageConfigFile, Collections.emptyList());
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
        new BStatsMetrics(plugin, metrics, Collections.emptyMap(), mock(Compatibility.class), mock(InstructionApi.class));
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testCustomChartCallableGetsUpdates(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        setupLogger();
        final Plugin plugin = mock(Plugin.class);

        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final PluginDescriptionFile description = new PluginDescriptionFile("BetonQuest", "2.0.0", "org.betonquest.betonquest.BetonQuest");
        when(plugin.getDescription()).thenReturn(description);

        when(plugin.getServer()).thenReturn(server);
        when(server.getBukkitVersion()).thenReturn("1.18.2-R0.1");

        final Metrics bstatsMetrics = mock(Metrics.class);
        final ArgumentCaptor<CustomChart> chartArgumentCaptor = ArgumentCaptor.forClass(CustomChart.class);

        final Map<ReadableIdentifier, Void> ids = new HashMap<>();

        final ReadableIdentifier firstId = mock(ReadableIdentifier.class);
        when(firstId.readRawInstruction()).thenReturn(TEST_INSTRUCTION);
        final Instruction firstInstruction = new DefaultInstruction(mock(Placeholders.class),
                mock(QuestPackageManager.class), questPackage, firstId, mock(ArgumentParsers.class), TEST_INSTRUCTION);

        ids.put(firstId, null);
        final Map<String, Void> types = new HashMap<>();
        types.put(TEST_INSTRUCTION, null);

        final InstructionMetricsSupplier<ReadableIdentifier> metricsSupplier = new CompositeInstructionMetricsSupplier<>(ids::keySet, types::keySet);

        final InstructionApi instructionApi = mock(InstructionApi.class);
        when(instructionApi.createInstruction(firstId, TEST_INSTRUCTION)).thenReturn(firstInstruction);

        final ReadableIdentifier secondId = mock(ReadableIdentifier.class);
        when(secondId.readRawInstruction()).thenReturn(TEST_INSTRUCTION);
        final Instruction secondInstruction = new DefaultInstruction(mock(Placeholders.class), mock(QuestPackageManager.class),
                questPackage, secondId, mock(ArgumentParsers.class), TEST_INSTRUCTION);
        when(instructionApi.createInstruction(secondId, TEST_INSTRUCTION)).thenReturn(secondInstruction);

        final ReadableIdentifier thirdId = mock(ReadableIdentifier.class);
        when(thirdId.readRawInstruction()).thenReturn(OTHER_INSTRUCTION);
        final Instruction thirdInstruction = new DefaultInstruction(mock(Placeholders.class), mock(QuestPackageManager.class),
                questPackage, thirdId, mock(ArgumentParsers.class), OTHER_INSTRUCTION);
        when(instructionApi.createInstruction(thirdId, OTHER_INSTRUCTION)).thenReturn(thirdInstruction);

        new BStatsMetrics(plugin, bstatsMetrics, Map.of("id", metricsSupplier), mock(Compatibility.class), instructionApi);

        verify(bstatsMetrics, times(6)).addCustomChart(chartArgumentCaptor.capture());
        final List<CustomChart> customCharts = chartArgumentCaptor.getAllValues();
        final CustomChart countChart = customCharts.get(2);
        final CustomChart enabledChart = customCharts.get(3);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":1}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        types.put(OTHER_INSTRUCTION, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":1}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        ids.put(secondId, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"test\":2}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"test\":1}}}", enabledChart);

        ids.put(thirdId, null);

        assertCollectedChartData("{\"chartId\":\"idCount\",\"data\":{\"values\":{\"other\":1,\"test\":2}}}", countChart);
        assertCollectedChartData("{\"chartId\":\"idEnabled\",\"data\":{\"values\":{\"other\":1,\"test\":1}}}", enabledChart);
    }

    private void assertCollectedChartData(final String expected, final CustomChart chart) {
        final String actual = chart.getRequestJsonObject((s, throwable) -> fail("Encountered error: " + s), true).toString();
        assertEquals(expected, actual, "generated chart data did not match");
    }
}
