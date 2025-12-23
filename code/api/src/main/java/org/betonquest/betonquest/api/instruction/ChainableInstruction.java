package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface ChainableInstruction {

    <T> Variable<T> getNext(InstructionArgumentParser<T> argument, @Nullable T defaultValue);

    <T> Variable<List<T>> getNextList(InstructionArgumentParser<T> argument, @Nullable T defaultValue);

    <T> Variable<Optional<T>> getOptional(String argumentKey, InstructionArgumentParser<T> argument, @Nullable T defaultValue);

    <T> Variable<Optional<List<T>>> getOptionalList(String argumentKey, InstructionArgumentParser<T> argument, @Nullable T defaultValue);
}
