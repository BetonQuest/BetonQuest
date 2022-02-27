package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JournalEvent tests.
 */
class JournalEventTest {

    /**
     * Mocked Minecraft Bukkit server.
     */
    private static Server server;

    /**
     * Logger used by the mock Bukkit server instance.
     */
    private static Logger bukkitLogger;

    /**
     * Create JournalEvent test class.
     */
    public JournalEventTest() {}

    @BeforeAll
    static void initializeBukkit() {
        bukkitLogger = LogValidator.getSilentLogger();
        server = mock(Server.class);
        when(server.getLogger()).thenReturn(bukkitLogger);
        Bukkit.setServer(server);
    }

    @AfterEach
    void resetServerMock() {
        reset(server);
    }

    private QuestPackage setupQuestPackage(final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackage("test", packageConfigFile, Collections.emptyList());
    }

    private LogValidator setupLogger() {
        final Logger logger = LogValidator.getSilentLogger();
        logger.setParent(bukkitLogger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(logger);

        final PluginManager pluginManager = mock(PluginManager.class);
        when(pluginManager.getPlugins()).thenReturn(new Plugin[]{plugin});
        when(server.getPluginManager()).thenReturn(pluginManager);

        logValidator.flush();

        return logValidator;
    }

    @Test
    void constructJournalUpdateEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal update");
        assertDoesNotThrow(() -> new JournalEvent(instruction), "journal event update action could not be created");
    }

    @Test
    void constructJournalAddEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal add quest_started");
        assertDoesNotThrow(() -> new JournalEvent(instruction), "journal event add action could not be created");
    }

    @Test
    void constructJournalAddEventWithoutPageReference(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal add");
        assertThrows(InstructionParseException.class, () -> new JournalEvent(instruction), "journal event add action without page reference should throw an exception when created");
    }

    @Test
    void constructJournalDeleteEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal delete quest_available");
        assertDoesNotThrow(() -> new JournalEvent(instruction), "journal event delete action could not be created");
    }

    @Test
    void constructJournalDeleteEventWithoutPageReference(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal delete");
        assertThrows(InstructionParseException.class, () -> new JournalEvent(instruction), "journal event delete action without page reference should throw an exception when created");
    }

    @Test
    void constructInvalidJournalEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal invalid");
        assertThrows(InstructionParseException.class, () -> new JournalEvent(instruction), "invalid action of journal event should throw an exception when created");
    }

    @Test
    void executeJournalAddEventWithoutPlayer(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, InstructionParseException, QuestRuntimeException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal add quest_started");
        final JournalEvent event = new JournalEvent(instruction);

        event.execute(null);

        verify(server, never()).getOnlinePlayers();
    }

    @Test
    void executeJournalUpdateEventWithoutPlayer(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, InstructionParseException, QuestRuntimeException {
        setupLogger();
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal update");
        final JournalEvent event = new JournalEvent(instruction);

        event.execute(null);

        verify(server, never()).getOnlinePlayers();
    }
}
