package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.event.journal.JournalEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactoryAdapter;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JournalEvent tests.
 */
@ExtendWith(BetonQuestLoggerService.class)
class JournalEventFactoryIntegrationTest {

    /**
     * Mocked Minecraft Bukkit server.
     */
    private final Server server = mock(Server.class);

    /**
     * Mocked database Saver.
     */
    private final Saver saver = mock(Saver.class);

    /**
     * The journal factory used to create journal events in tests.
     */
    private final QuestEventFactoryAdapter journalFactory = new QuestEventFactoryAdapter(new JournalEventFactory(saver, server));

    /**
     * Create JournalEvent test class.
     */
    public JournalEventFactoryIntegrationTest() {
    }

    @AfterEach
    void resetServerMock() {
        reset(server);
        reset(saver);
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

    @Test
    void constructJournalUpdateEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal update");
        assertDoesNotThrow(() -> journalFactory.parseEventInstruction(instruction), "journal event update action could not be created");
    }

    @Test
    void constructJournalAddEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal add quest_started");
        assertDoesNotThrow(() -> journalFactory.parseEventInstruction(instruction), "journal event add action could not be created");
    }

    @Test
    void constructJournalAddEventWithoutPageReference(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal add");
        assertThrows(InstructionParseException.class, () -> journalFactory.parseEventInstruction(instruction), "journal event add action without page reference should throw an exception when created");
    }

    @Test
    void constructJournalDeleteEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal delete quest_available");
        assertDoesNotThrow(() -> journalFactory.parseEventInstruction(instruction), "journal event delete action could not be created");
    }

    @Test
    void constructJournalDeleteEventWithoutPageReference(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal delete");
        assertThrows(InstructionParseException.class, () -> journalFactory.parseEventInstruction(instruction), "journal event delete action without page reference should throw an exception when created");
    }

    @Test
    void constructInvalidJournalEvent(@TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final QuestPackage questPackage = setupQuestPackage(questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "journal invalid");
        assertThrows(InstructionParseException.class, () -> journalFactory.parseEventInstruction(instruction), "invalid action of journal event should throw an exception when created");
    }
}
