package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.variable.Variable;

import java.util.List;

public interface InstructionChainRetriever {

    <T> Variable<T> get();

    <T> Variable<List<T>> getList();
}
