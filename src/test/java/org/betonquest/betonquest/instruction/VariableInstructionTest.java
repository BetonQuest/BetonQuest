package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class VariableInstructionTest {

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    protected QuestPackage questPackage;

    @Test
    void variableInstructionShouldThrowExceptionWhenInstructionDoesNotStartAndEndWithPercentCharacter(final BetonQuestLogger log) {
        assertThrows(IllegalArgumentException.class, () -> {
            new VariableInstruction(log, questPackage, null, "instruction");
        }, "Should throw an exception");
    }

    @Test
    void variableInstructionShouldNotThrowExceptionWhenInstructionStartsAndEndsWithPercentCharacter(final BetonQuestLogger log) {
        assertDoesNotThrow(() -> {
            new VariableInstruction(log, questPackage, null, "%instruction%");
        }, "Should not throw an exception");
    }

    @Test
    void copyShouldReturnNewVariableInstructionWithSameProperties(final BetonQuestLogger log) throws ObjectNotFoundException {
        final VariableInstruction original = new VariableInstruction(log, questPackage, null, "%instruction%");
        final VariableInstruction copy = original.copy();
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertEquals(original.getID(), copy.getID(), "Should have the same ID");
    }

    @Test
    void copyWithNewIDShouldReturnNewVariableInstructionWithNewID(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) throws ObjectNotFoundException {
        final VariableInstruction original = new VariableInstruction(log, questPackage, null, "%instruction%");
        final Instruction copy = original.copy(new VariableID(loggerFactory, questPackage, "%newID%"));
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertNotEquals(original.getID(), copy.getID(), "Should have different ID");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void partsShouldBeSplitByDot(final BetonQuestLogger log) throws QuestException {
        final VariableInstruction instruction = new VariableInstruction(log, questPackage, null, "%instruction.part1.part2%");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part1", instruction.next(), "Should return the next part");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part2", instruction.next(), "Should return the next part");
    }
}
