package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;

import static org.mockito.Mockito.*;

/**
 * Instruction where everything except package and instruction string is mocked.
 */
public class MockedInstruction extends Instruction {

    /**
     * Creates a new mocked instruction.
     *
     * @param pack        the source package
     * @param instruction the instruction string
     * @throws QuestException when the instruction could not be created
     */
    public MockedInstruction(final QuestPackage pack, final String instruction) throws QuestException {
        super(mock(Variables.class), mock(QuestPackageManager.class), pack, null, instruction);
    }
}
