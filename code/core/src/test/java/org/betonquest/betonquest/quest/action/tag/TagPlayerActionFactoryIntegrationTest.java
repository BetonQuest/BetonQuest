package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionMock;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.lib.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.lib.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
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
 * Integration test for {@link TagPlayerActionFactory}.
 */
@ExtendWith(BetonQuestLoggerExtension.class)
@ExtendWith(MockitoExtension.class)
class TagPlayerActionFactoryIntegrationTest {

    /**
     * Mocked PlayerDataStorage.
     */
    @Mock
    private Persistence persistence;

    /**
     * Mocked database Saver.
     */
    @Mock
    private Saver saver;

    /**
     * Mocked Profile Provider.
     */
    @Mock
    private ProfileProvider profileProvider;

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
    void testCreateTagAddActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation add could not be created");
    }

    @Test
    void testCreateTagAddActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation add could not be created");
    }

    @Test
    void testCreateTagAddActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeleteActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation del could not be created");
    }

    @Test
    void testCreateTagDelActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation del could not be created");
    }

    @Test
    void testCreateTagDelActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidTagAction(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation invalid should throw an exception when created");
    }

    @Test
    void testCreateTagAddPlayerlessActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation add could not be created");
    }

    @Test
    void testCreateTagAddPlayerlessActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation add could not be created");
    }

    @Test
    void testCreateTagAddPlayerlessActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeletePlayerlessActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeletePlayerlessActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation delete could not be created");
    }

    @Test
    void testCreateTagDeletePlayerlessActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelPlayerlessActionWithMultipleTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation del could not be created");
    }

    @Test
    void testCreateTagDelPlayerlessActionWithOneTag(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation del could not be created");
    }

    @Test
    void testCreateTagDelPlayerlessActionWithoutTags(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidPlayerlessTagAction(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation invalid should throw an exception when created");
    }
}
