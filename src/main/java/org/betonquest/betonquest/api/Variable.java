package org.betonquest.betonquest.api;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Represents a variable in conversations.
 */
abstract public class Variable {

    /**
     * Stores instruction string for the condition.
     */
    protected Instruction instruction;

    /**
     * A variable marked as static can be executed even if the profile in the
     * {@link #getValue(Profile)} method is null
     */
    protected boolean staticness;

    /**
     * Creates new instance of the variable. The variable should parse the
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object representing this variable; you need to
     *                    extract all required data from it and throw
     *                    {@link InstructionParseException} if there is anything wrong
     */
    public Variable(final Instruction instruction) {
        this.instruction = instruction;
    }

    /**
     * Get the instruction object for this variable
     *
     * @return The instruction
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * Get the staticness of a variable
     *
     * @return The staticness
     */
    public boolean isStaticness() {
        return staticness;
    }

    /**
     * This method should return a resolved value of variable for given profile.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this variable
     */
    public abstract String getValue(Profile profile);

    @Override
    public String toString() {
        return instruction.getInstruction();
    }
}
