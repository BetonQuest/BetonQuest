package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.variable.VariableID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test the {@link VariableInstruction}.
 */
@ExtendWith(MockitoExtension.class)
class VariableInstructionTest {

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    protected QuestPackage questPackage;

    private VariableInstruction createNoIDInstruction(final String instruction) throws QuestException {
        return new VariableInstruction(mock(Variables.class), mock(QuestPackageManager.class),
                questPackage, new NoID(mock(QuestPackageManager.class), questPackage), instruction);
    }

    @Test
    void variableInstructionShouldThrowExceptionWhenInstructionDoesNotStartAndEndWithPercentCharacter() {
        assertThrows(QuestException.class, () -> {
            createNoIDInstruction("instruction");
        }, "Should throw an exception");
    }

    @Test
    void variableInstructionShouldNotThrowExceptionWhenInstructionStartsAndEndsWithPercentCharacter() {
        assertDoesNotThrow(() -> {
            createNoIDInstruction("%instruction%");
        }, "Should not throw an exception");
    }

    @Test
    void copyShouldReturnNewVariableInstructionWithSameProperties() throws QuestException {
        final VariableInstruction original = createNoIDInstruction("%instruction%");
        final VariableInstruction copy = original.copy();
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertEquals(original.getID(), copy.getID(), "Should have the same ID");
    }

    @Test
    void copyWithNewIDShouldReturnNewVariableInstructionWithNewID() throws QuestException {
        final VariableID variableID1 = mock(VariableID.class);
        final VariableID variableID2 = mock(VariableID.class);
        final VariableInstruction original = new VariableInstruction(mock(Variables.class), mock(QuestPackageManager.class),
                questPackage, variableID1, "%instruction%");
        final DefaultInstruction copy = original.copy(variableID2);
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertNotEquals(original.getID(), copy.getID(), "Should have different ID");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void partsShouldBeSplitByDot() throws QuestException {
        final VariableInstruction instruction = createNoIDInstruction("%instruction.part1.part2%");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part1", instruction.next(), "Should return the next part");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part2", instruction.next(), "Should return the next part");
    }
}
