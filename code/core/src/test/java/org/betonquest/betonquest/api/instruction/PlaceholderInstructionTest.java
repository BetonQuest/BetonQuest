package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.id.placeholder.PlaceholderIdentifierFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test the {@link PlaceholderInstruction}.
 */
@ExtendWith(MockitoExtension.class)
class PlaceholderInstructionTest {

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    protected QuestPackage questPackage;

    private PlaceholderInstruction createNoIDInstruction(final String instruction) throws QuestException {
        return new PlaceholderInstruction(mock(Placeholders.class), mock(QuestPackageManager.class),
                questPackage, new NoID(questPackage), mock(ArgumentParsers.class), instruction);
    }

    @Test
    void placeholderInstructionShouldThrowExceptionWhenInstructionDoesNotStartAndEndWithPercentCharacter() {
        assertThrows(QuestException.class, () -> {
            createNoIDInstruction("instruction");
        }, "Should throw an exception");
    }

    @Test
    void placeholderInstructionShouldNotThrowExceptionWhenInstructionStartsAndEndsWithPercentCharacter() {
        assertDoesNotThrow(() -> {
            createNoIDInstruction("%instruction%");
        }, "Should not throw an exception");
    }

    @Test
    void copyShouldReturnNewPlaceholderInstructionWithSameProperties() throws QuestException {
        final PlaceholderInstruction original = createNoIDInstruction("%instruction%");
        final PlaceholderInstruction copy = original.copy();
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertEquals(original.getID(), copy.getID(), "Should have the same ID");
    }

    @Test
    void copyWithNewIDShouldReturnNewPlaceholderInstructionWithNewID() throws QuestException {
        final PlaceholderIdentifier placeholderID1 = mock(PlaceholderIdentifier.class);
        final PlaceholderIdentifier placeholderID2 = mock(PlaceholderIdentifier.class);
        final PlaceholderInstruction original = new PlaceholderInstruction(mock(Placeholders.class), mock(QuestPackageManager.class),
                questPackage, placeholderID1, mock(ArgumentParsers.class), "%instruction%");
        final Instruction copy = original.copy(placeholderID2);
        assertEquals(original.toString(), copy.toString(), "Should have the same instruction");
        assertNotEquals(original.getID(), copy.getID(), "Should have different ID");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void partsShouldBeSplitByDot() throws QuestException {
        final PlaceholderInstruction instruction = createNoIDInstruction("%instruction.part1.part2%");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part1", instruction.nextElement(), "Should return the next part");
        assertTrue(instruction.hasNext(), "Should have more parts");
        assertEquals("part2", instruction.nextElement(), "Should return the next part");
    }

    @Test
    void shouldReferenceOtherPack() throws QuestException {
        final QuestPackageManager packageManager = mock(QuestPackageManager.class);
        final QuestPackage otherPack = mock(QuestPackage.class);
        when(packageManager.getPackage("OtherPack")).thenReturn(otherPack);
        final PlaceholderIdentifierFactory factory = new PlaceholderIdentifierFactory(packageManager);
        final PlaceholderIdentifier placeholderIdentifier = factory.parseIdentifier(questPackage, "%OtherPack>instruction.part%");

        assertEquals(otherPack, placeholderIdentifier.getPackage(), "Cross package reference should resolve to correct pack");
        final PlaceholderInstruction instruction = new PlaceholderInstruction(mock(Placeholders.class), packageManager,
                placeholderIdentifier.getPackage(), new NoID(placeholderIdentifier.getPackage()),
                mock(ArgumentParsers.class), placeholderIdentifier.readRawInstruction());
        assertEquals("instruction.part", instruction.toString(), "Instruction should not contain pack");
    }
}
