package org.betonquest.betonquest.api.instruction.argument;

/**
 * Objectified parser for the Instruction to get a {@link T} from variables, quest package manager, package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface InstructionIdentifierArgument<T> extends InstructionArgumentParser<T> {

}
