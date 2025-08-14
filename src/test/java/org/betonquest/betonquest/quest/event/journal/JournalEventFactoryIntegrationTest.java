package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.SingletonLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapterFactory;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.bukkit.Server;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        final QuestPackageImpl test = new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(factory, logger), "test", packageConfigFile, Collections.emptyList());
        test.applyQuestTemplates(Map.of());
        test.getConfig().set("journal.quest_started", "?");
        return test;
    }

    private EventAdapterFactory createJournalEventFactory(final BetonQuestLogger logger) {
        final ProfileProvider profileProvider = new UUIDProfileProvider(mock(Server.class));
        final JournalEventFactory journalEventFactory = new JournalEventFactory(new SingletonLoggerFactory(logger), mock(PluginMessage.class), dataStorage, InstantSource.fixed(now), saver, profileProvider);
        return new EventAdapterFactory(mock(BetonQuestLoggerFactory.class), mock(QuestTypeApi.class), journalEventFactory, journalEventFactory);
    }

    @Test
    void constructJournalUpdateEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal update");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event update action could not be created");
    }

    @Test
    void constructJournalAddEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal add quest_started");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event add action could not be created");
    }

    @Test
    void constructJournalAddEventWithoutPageReference(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal add");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "journal event add action without page reference should throw an exception when created");
    }

    @Test
    void constructJournalDeleteEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal delete quest_started");
        assertDoesNotThrow(() -> journalFactory.parseInstruction(instruction), "journal event delete action could not be created");
    }

    @Test
    void constructJournalDeleteEventWithoutPageReference(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal delete");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "journal event delete action without page reference should throw an exception when created");
    }

    @Test
    void constructInvalidJournalEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final EventAdapterFactory journalFactory = createJournalEventFactory(logger);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, new NoID(questPackage), "journal invalid");
        assertThrows(QuestException.class, () -> journalFactory.parseInstruction(instruction), "invalid action of journal event should throw an exception when created");
    }
}
