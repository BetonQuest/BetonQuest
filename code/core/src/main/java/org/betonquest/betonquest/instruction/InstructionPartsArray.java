package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.tokenizer.TokenizerException;

import java.util.List;

/**
 * Represents the parts of an instruction.
 */
public class InstructionPartsArray implements InstructionParts {
    /**
     * The parts of the instruction.
     */
    private final String[] parts;

    /**
     * The index of the current part.
     */
    private int index;

    /**
     * Constructs the instruction parts from the given instruction and tokenizer.
     *
     * @param tokenizer   The tokenizer to use.
     * @param instruction The instruction to tokenize.
     * @throws TokenizerException If the tokenizer fails to tokenize the instruction.
     */
    public InstructionPartsArray(final Tokenizer tokenizer, final String instruction) throws TokenizerException {
        this.parts = tokenizer.tokens(instruction);
        this.index = 0;
    }

    /**
     * Constructs the instruction parts object from the given instruction parts object.
     *
     * @param instructionParts The instruction parts to use.
     */
    public InstructionPartsArray(final InstructionParts instructionParts) {
        this.parts = instructionParts.getParts().toArray(new String[0]);
        this.index = 0;
    }

    @Override
    public String next() throws QuestException {
        if (this.index >= this.parts.length - 1) {
            throw new QuestException("No part left");
        }
        return this.parts[++this.index];
    }

    @Override
    public String current() {
        return this.parts[this.index];
    }

    @Override
    public boolean hasNext() {
        return this.index < this.parts.length - 1;
    }

    @Override
    public int size() {
        return this.parts.length;
    }

    @Override
    public String getPart(final int index) throws QuestException {
        if (index < 0 || index >= this.parts.length) {
            throw new QuestException("No part at index " + index);
        }
        return this.parts[index];
    }

    @Override
    public List<String> getParts() {
        return List.of(parts);
    }
}
