package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
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
 * Integration test for {@link TagPlayerEventFactory}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class TagPlayerEventFactoryIntegrationTest {
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

    @Test
    void testCreateTagAddEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action add could not be created");
    }

    @Test
    void testCreateTagAddEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add tag-1");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action add could not be created");
    }

    @Test
    void testCreateTagAddEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add");
        assertThrows(QuestException.class, () -> factory.parseEvent(instruction), "tag event action add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete tag-1");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete");
        assertThrows(QuestException.class, () -> factory.parseEvent(instruction), "tag event action delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action del could not be created");
    }

    @Test
    void testCreateTagDelEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del tag-1");
        assertDoesNotThrow(() -> factory.parseEvent(instruction), "tag event action del could not be created");
    }

    @Test
    void testCreateTagDelEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del");
        assertThrows(QuestException.class, () -> factory.parseEvent(instruction), "tag event action del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidTagEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> factory.parseEvent(instruction), "tag event action invalid should throw an exception when created");
    }

    @Test
    void testCreateTagAddStaticEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action add could not be created");
    }

    @Test
    void testCreateTagAddStaticEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add tag-1");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action add could not be created");
    }

    @Test
    void testCreateTagAddStaticEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag add");
        assertThrows(QuestException.class, () -> factory.parseStaticEvent(instruction), "tag event action add without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete tag-1");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action delete could not be created");
    }

    @Test
    void testCreateTagDeleteStaticEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag delete");
        assertThrows(QuestException.class, () -> factory.parseStaticEvent(instruction), "tag event action delete without tags should throw an exception when created");
    }

    @Test
    void testCreateTagDelStaticEventWithMultipleTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del tag-1,tag-2,tag-3");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action del could not be created");
    }

    @Test
    void testCreateTagDelStaticEventWithOneTag(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del tag-1");
        assertDoesNotThrow(() -> factory.parseStaticEvent(instruction), "tag event action del could not be created");
    }

    @Test
    void testCreateTagDelStaticEventWithoutTags(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag del");
        assertThrows(QuestException.class, () -> factory.parseStaticEvent(instruction), "tag event action del without tags should throw an exception when created");
    }

    @Test
    void testCreateInvalidStaticTagEvent(final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory) throws IOException, InvalidConfigurationException {
        final TagPlayerEventFactory factory = new TagPlayerEventFactory(dataStorage, saver);
        final QuestPackage questPackage = setupQuestPackage(logger, questPackagesDirectory);

        final Instruction instruction = new Instruction(logger, questPackage, null, "tag invalid tag-1,tag-2");
        assertThrows(QuestException.class, () -> factory.parseStaticEvent(instruction), "tag event action invalid should throw an exception when created");
    }
}
