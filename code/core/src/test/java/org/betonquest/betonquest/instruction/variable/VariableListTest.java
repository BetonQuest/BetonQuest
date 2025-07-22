package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for constructing {@link VariableList}s and validating constant values.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class VariableListTest {

    /**
     * The QuestPackage used for generating instructions.
     */
    private QuestPackage questPackage;

    /**
     * The variable processor to create variables.
     */
    @Mock
    private VariableProcessor variableProcessor;

    @BeforeEach
    void setupQuestPackage() {
        final QuestPackage pack = mock(QuestPackage.class);
        final MultiConfiguration config = mock(MultiConfiguration.class);
        lenient().when(pack.getConfig()).thenReturn(config);
        lenient().when(config.getString("events.a")).thenReturn("?");
        lenient().when(config.getString("events.b")).thenReturn("?");
        lenient().when(config.getString("events.c")).thenReturn("?");
        this.questPackage = pack;
    }

    private Variable<List<EventID>> getVariableList(final String input) throws QuestException {
        return new VariableList<>(variableProcessor, questPackage, input, value -> new EventID(questPackage, value));
    }

    @Test
    void constructNonBackedList() {
        assertThrows(QuestException.class, () -> getVariableList("a,z,c"),
                "Non existing event ID should throw an exception when validating");
    }

    @Test
    void constructBackedList() {
        assertDoesNotThrow(() -> getVariableList("a,c"),
                "Getting existing variables should not fail");
    }

    @Test
    void constructEmptyList() {
        final Variable<List<EventID>> list = assertDoesNotThrow(() -> getVariableList(",,"),
                "Parsing an empty list should not fail");
        assertDoesNotThrow(() -> list.getValue(null),
                "Empty list should not fail getting values");
    }

    @Test
    void constructBackedListWithVariable() {
        assertDoesNotThrow(() -> getVariableList("a,%bVar%,c"),
                "Validating existing variables should not fail");
    }

    @Test
    void getNonBackedVariableFromInstruction() {
        assertThrows(QuestException.class, () -> getVariableList("a,%bVar%,z,c"),
                "Validating non-existing constant with also a variable should fail");
    }

    @Test
    void getListWithBackedVariable() throws QuestException {
        final VariableAdapter variable = mock(VariableAdapter.class);
        when(variable.getValue(any())).thenReturn("b");
        when(variableProcessor.create(questPackage, "%bVar%")).thenReturn(variable);
        final Variable<List<EventID>> list = assertDoesNotThrow(() -> getVariableList("a,%bVar%,c"),
                "Validating existing variables should not fail");
        assertDoesNotThrow(() -> list.getValue(null), "Getting existing variable should not fail");
    }

    @Test
    void constructListWithNonBackedVariable() throws QuestException {
        when(variableProcessor.create(questPackage, "%otherVar%"))
                .thenThrow(new QuestException("The variable does not exist"));
        assertThrows(QuestException.class, () -> getVariableList("a,%otherVar%,c"),
                "Parsing non-existing variable should fail");
    }
}
