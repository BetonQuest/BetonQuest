package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.instruction.argument.parser.ArgumentConverter;
import org.betonquest.betonquest.api.instruction.argument.parser.IdentifierArgumentConverter;
import org.betonquest.betonquest.api.instruction.argument.parser.InstructionIdentifierArgumentConverter;
import org.betonquest.betonquest.api.instruction.argument.parser.PackageArgumentConverter;

/**
 * The Instruction. Primary object for input parsing.
 */
public interface Instruction extends InstructionParts, ArgumentConverter, PackageArgumentConverter, IdentifierArgumentConverter, InstructionIdentifierArgumentConverter {

}
