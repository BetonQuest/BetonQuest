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
class TagPlayerActionFactoryIT {

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

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(loggerFactory, logger), "test", packageConfigFile, Collections.emptyList());
    }

    @Test
    void create_tag_add_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation add could not be created");
    }

    @Test
    void create_tag_add_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation add could not be created");
    }

    @Test
    void create_tag_add_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation add without tags should throw an exception when created");
    }

    @Test
    void create_tag_delete_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation delete could not be created");
    }

    @Test
    void create_tag_delete_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation delete could not be created");
    }

    @Test
    void create_tag_delete_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation delete without tags should throw an exception when created");
    }

    @Test
    void create_tag_del_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation del could not be created");
    }

    @Test
    void create_tag_del_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayer(instruction), "tag action operation del could not be created");
    }

    @Test
    void create_tag_del_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation del without tags should throw an exception when created");
    }

    @Test
    void create_invalid_tag_action(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayer(instruction), "tag action operation invalid should throw an exception when created");
    }

    @Test
    void create_tag_add_playerless_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation add could not be created");
    }

    @Test
    void create_tag_add_playerless_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation add could not be created");
    }

    @Test
    void create_tag_add_playerless_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag add");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation add without tags should throw an exception when created");
    }

    @Test
    void create_tag_delete_playerless_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation delete could not be created");
    }

    @Test
    void create_tag_delete_playerless_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation delete could not be created");
    }

    @Test
    void create_tag_delete_playerless_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag delete");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation delete without tags should throw an exception when created");
    }

    @Test
    void create_tag_del_playerless_action_with_multiple_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation del could not be created");
    }

    @Test
    void create_tag_del_playerless_action_with_one_tag(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del tag-1");
        assertDoesNotThrow(() -> tagFactory.parsePlayerless(instruction), "tag action operation del could not be created");
    }

    @Test
    void create_tag_del_playerless_action_without_tags(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag del");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation del without tags should throw an exception when created");
    }

    @Test
    void create_invalid_playerless_tag_action(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException, QuestException {
        final TagPlayerActionFactory tagFactory = new TagPlayerActionFactory(persistence, saver, profileProvider);
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);

        final Instruction instruction = new InstructionMock(questPackage, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> tagFactory.parsePlayerless(instruction), "tag action operation invalid should throw an exception when created");
    }
}
