package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.lib.instruction.argument.DefaultListArgument;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.ConfigurationOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for constructing {@link DefaultListArgument}s and validating constant values.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class DefaultListArgumentTest {

    /**
     * The QuestPackage used for generating instructions.
     */
    private QuestPackage questPackage;

    /**
     * The {@link Placeholders} to create placeholders.
     */
    @Mock
    private Placeholders placeholders;

    @BeforeEach
    void setupQuestPackage() {
        final QuestPackage pack = mock(QuestPackage.class);
        final MultiConfiguration config = mock(MultiConfiguration.class);
        lenient().when(pack.getConfig()).thenReturn(config);
        lenient().when(config.getString("events.a")).thenReturn("?");
        lenient().when(config.getString("events.b")).thenReturn("?");
        lenient().when(config.getString("events.c")).thenReturn("?");
        final ConfigurationOptions configurationOptions = mock(ConfigurationOptions.class);
        lenient().when(config.options()).thenReturn(configurationOptions);
        lenient().when(configurationOptions.pathSeparator()).thenReturn('.');
        this.questPackage = pack;
    }

    private Argument<List<EventID>> getArgumentList(final String input) throws QuestException {
        return new DefaultListArgument<>(placeholders, questPackage, input, value -> new EventID(mock(Placeholders.class), mock(QuestPackageManager.class), questPackage, value));
    }

    @Test
    void constructNonBackedList() {
        assertThrows(QuestException.class, () -> getArgumentList("a,z,c"),
                "Non existing event ID should throw an exception when validating");
    }

    @Test
    void constructBackedList() {
        assertDoesNotThrow(() -> getArgumentList("a,c"),
                "Getting existing placeholders should not fail");
    }

    @Test
    void constructEmptyList() {
        final Argument<List<EventID>> list = assertDoesNotThrow(() -> getArgumentList(",,"),
                "Parsing an empty list should not fail");
        assertDoesNotThrow(() -> list.getValue(null),
                "Empty list should not fail getting values");
    }

    @Test
    void constructBackedListWithPlaceholder() {
        assertDoesNotThrow(() -> getArgumentList("a,%bVar%,c"),
                "Validating existing placeholders should not fail");
    }

    @Test
    void getNonBackedPlaceholderFromInstruction() {
        assertThrows(QuestException.class, () -> getArgumentList("a,%bVar%,z,c"),
                "Validating non-existing constant with also a placeholder should fail");
    }

    @Test
    void getListWithBackedPlaceholder() throws QuestException {
        final Argument<String> argument = profile -> "b";
        when(placeholders.create(questPackage, "%bVar%")).thenReturn(argument);
        final Argument<List<EventID>> list = assertDoesNotThrow(() -> getArgumentList("a,%bVar%,c"),
                "Validating existing placeholders should not fail");
        assertDoesNotThrow(() -> list.getValue(null), "Getting existing placeholder should not fail");
    }

    @Test
    void constructListWithNonBackedPlaceholder() throws QuestException {
        when(placeholders.create(questPackage, "%otherVar%"))
                .thenThrow(new QuestException("The placeholder does not exist"));
        assertThrows(QuestException.class, () -> getArgumentList("a,%otherVar%,c"),
                "Parsing non-existing placeholder should fail");
    }
}
