package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link TagGlobalEventFactory}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class TagGlobalEventFactoryIntegrationTest {
    /**
     * Mocked BetonQuest plugin.
     */
    @Mock
    private BetonQuest betonQuest;

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(factory, logger), "test", packageConfigFile, Collections.emptyList());
    }

    @Test
    void testCreateGlobalTagAddEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action add could not be created");
    }

    @Test
    void testCreateTagAddEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action add could not be created");
    }

    @Test
    void testCreateTagAddEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag event action add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag event action delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action del could not be created");
    }

    @Test
    void testCreateTagDelEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag event action del could not be created");
    }

    @Test
    void testCreateTagDelEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag event action del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidTagEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag event action invalid should throw an exception when created");
    }

    @Test
    void testCreateTagAddStaticEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action add could not be created");
    }

    @Test
    void testCreateTagAddStaticEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action add could not be created");
    }

    @Test
    void testCreateTagAddStaticEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag event action add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag event action delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelStaticEventWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action del could not be created");
    }

    @Test
    void testCreateTagDelStaticEventWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag event action del could not be created");
    }

    @Test
    void testCreateTagDelStaticEventWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag event action del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidStaticTagEvent(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalEventFactory tagFactory = new TagGlobalEventFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(questPackage, null, "globaltag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag event action invalid should throw an exception when created");
    }
}
