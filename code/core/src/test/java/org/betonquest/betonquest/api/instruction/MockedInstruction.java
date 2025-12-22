package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratableArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.StringParser;
import org.betonquest.betonquest.api.quest.Variables;

import static org.mockito.Mockito.*;

/**
 * Instruction where everything except package and instruction string is mocked.
 */
public class MockedInstruction extends DefaultInstruction {

    /**
     * Mocked argument parsers.
     */
    private static final ArgumentParsers ARGUMENT_PARSERS = mock(ArgumentParsers.class);

    static {
        when(ARGUMENT_PARSERS.string()).thenReturn(new DecoratableArgument<>(new StringParser()));
    }

    /**
     * Creates a new mocked instruction.
     *
     * @param pack        the source package
     * @param instruction the instruction string
     * @throws QuestException when the instruction could not be created
     */
    public MockedInstruction(final QuestPackage pack, final String instruction) throws QuestException {
        super(mock(Variables.class), mock(QuestPackageManager.class), pack, null, ARGUMENT_PARSERS, instruction);
    }
}
