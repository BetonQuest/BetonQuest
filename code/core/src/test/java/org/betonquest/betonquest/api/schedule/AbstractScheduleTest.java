package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Abstract class that provides a basic test setup for testing schedules.
 * It mocks the {@link ScheduleIdentifier} of the schedule, it's {@link QuestPackage}
 * and the {@link ConfigurationSection} used for parsing the schedules' config.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
public abstract class AbstractScheduleTest {

    /**
     * {@link Placeholders} to create and resolve placeholders.
     */
    @Mock
    protected Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    @Mock
    protected QuestPackageManager packManager;

    /**
     * ID of the schedule to test.
     */
    @Mock
    protected ScheduleIdentifier scheduleID;

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    protected QuestPackage questPackage;

    /**
     * Configuration section of the schedule to test.
     */
    @Mock
    protected ConfigurationSection section;

    protected BetonQuestLoggerFactory loggerFactory;

    protected ArgumentParsers argumentParsers;

    /**
     * Before each test run prepare config and all mocks.
     *
     * @param loggerFactory the logger factory to use
     * @param logger        the logger to use
     * @throws QuestException if the setup failed
     */
    @BeforeEach
    protected void setupConfigs(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger) throws QuestException {
        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);
        this.loggerFactory = loggerFactory;

        final IdentifierRegistry identifierRegistry = new IdentifierTypeRegistry(logger);
        identifierRegistry.register(ActionIdentifier.class, new ActionIdentifierFactory(packManager));
        identifierRegistry.register(ItemIdentifier.class, new ItemIdentifierFactory(packManager));
        this.argumentParsers = new DefaultArgumentParsers((i, p) -> null, mock(TextParser.class), mock(Server.class), identifierRegistry);

        prepareConfig();
    }

    /**
     * Creates a mocked {@link SectionInstruction} with the provided mocks.
     *
     * @return the mocked instruction
     */
    protected SectionInstruction getMockedInstruction() {
        return new DefaultSectionInstruction(argumentParsers, placeholders, packManager, questPackage, section, loggerFactory);
    }

    /**
     * Method that creates a schedule instance with the provided ScheduleID and QuestPackage.
     *
     * @return the newly created schedule instance
     * @throws QuestException if parsing the schedule from the config failed
     */
    protected abstract Schedule createSchedule() throws QuestException;

    /**
     * Prepare the configuration section by configuring the mocks.
     */
    protected abstract void prepareConfig();
}
