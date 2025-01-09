package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.SingletonLoggerFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.modules.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.modules.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.legacy.QuestEventFactoryAdapter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link org.betonquest.betonquest.quest.event.journal}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class JournalEventFactoryIntegrationTest {
    /**
     * The current time used in the tests.
     */
    private final Instant now = Instant.now();

    /**
     * Mocked PlayerDataStorage.
     */
    @Mock
    private PlayerDataStorage dataStorage;

    /**
     * Mocked database Saver.
     */
    @Mock
    private Saver saver;

    /**
     * Create JournalEvent test class.
     */
    public JournalEventFactoryIntegrationTest() {
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
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(), "test", packageConfigFile, Collections.emptyList());
    }

    private QuestEventFactoryAdapter createJournalEventFactory(final BetonQuestLogger logger) {
        final JournalEventFactory journalEventFactory = new JournalEventFactory(new SingletonLoggerFactory(logger), dataStorage, InstantSource.fixed(now), saver);
        return new QuestEventFactoryAdapter(journalEventFactory, journalEventFactory);
    }

    @Test
    void constructJournalUpdateEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal update");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event update action could not be created");
    }

    @Test
    void constructJournalAddEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal add quest_started");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event add action could not be created");
    }

    @Test
    void constructJournalAddEventWithoutPageReference(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal add");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "journal event add action without page reference should throw an exception when created");
    }

    @Test
    void constructJournalDeleteEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal delete quest_available");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event delete action could not be created");
    }

    @Test
    void constructJournalDeleteEventWithoutPageReference(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal delete");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "journal event delete action without page reference should throw an exception when created");
    }

    @Test
    void constructInvalidJournalEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, ObjectNotFoundException {
        final QuestEventFactoryAdapter journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, new NoID(questPackage), "journal invalid");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "invalid action of journal event should throw an exception when created");
    }
}
