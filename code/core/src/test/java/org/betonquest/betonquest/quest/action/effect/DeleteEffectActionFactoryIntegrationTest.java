package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionMock;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.lib.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for {@link DeleteEffectActionFactory}.
 */
@ExtendWith(BetonQuestLoggerExtension.class)
class DeleteEffectActionFactoryIntegrationTest {

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory loggerFactory,
                                           final BetonQuestLogger logger,
                                           final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(loggerFactory, logger), "test",
                packageConfigFile, Collections.emptyList());
    }

    @Test
    void accepts_explicit_any_argument(final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuestLogger logger,
                                       @TempDir final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException, QuestException {
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);
        final Instruction instruction = new InstructionMock(questPackage, "deleffect any");

        assertDoesNotThrow(() -> new DeleteEffectActionFactory().parsePlayer(instruction));
    }

    @Test
    void rejects_missing_argument(final BetonQuestLoggerFactory loggerFactory,
                                  final BetonQuestLogger logger,
                                  @TempDir final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException, QuestException {
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);
        final Instruction instruction = new InstructionMock(questPackage, "deleffect");

        assertThrows(QuestException.class, () -> new DeleteEffectActionFactory().parsePlayer(instruction));
    }

    @Test
    void rejects_unknown_effect(final BetonQuestLoggerFactory loggerFactory,
                                final BetonQuestLogger logger,
                                @TempDir final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException, QuestException {
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);
        final Instruction instruction = new InstructionMock(questPackage, "deleffect NOT_A_REAL_EFFECT");

        assertThrows(QuestException.class, () -> new DeleteEffectActionFactory().parsePlayer(instruction));
    }
}
