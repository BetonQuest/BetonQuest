package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.MockedInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
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
 * Integration test for {@link TagGlobalActionFactory}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class TagGlobalActionFactoryIntegrationTest {

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
    void testCreateGlobalTagAddActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation add could not be created");
    }

    @Test
    void testCreateTagAddActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation add could not be created");
    }

    @Test
    void testCreateTagAddActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag action operation add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag action operation delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation del could not be created");
    }

    @Test
    void testCreateTagDelActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "globaltag action operation del could not be created");
    }

    @Test
    void testCreateTagDelActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag action operation del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidTagAction(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "globaltag action operation invalid should throw an exception when created");
    }

    @Test
    void testCreateTagAddStaticActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation add could not be created");
    }

    @Test
    void testCreateTagAddStaticActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation add could not be created");
    }

    @Test
    void testCreateTagAddStaticActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag action operation add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteStaticActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag action operation delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelStaticActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation del could not be created");
    }

    @Test
    void testCreateTagDelStaticActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "globaltag action operation del could not be created");
    }

    @Test
    void testCreateTagDelStaticActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag action operation del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidStaticTagAction(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagGlobalActionFactory tagFactory = new TagGlobalActionFactory(betonQuest);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new MockedInstruction(questPackage, "globaltag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "globaltag action operation invalid should throw an exception when created");
    }
}
