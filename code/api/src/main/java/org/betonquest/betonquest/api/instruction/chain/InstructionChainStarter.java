package org.betonquest.betonquest.api.instruction.chain;

public interface InstructionChainStarter {

    InstructionChainParser next();

    InstructionChainParser next(String argument);
}
