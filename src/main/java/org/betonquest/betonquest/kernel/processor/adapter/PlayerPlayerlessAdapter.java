package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to store the player and playerless variant.
 *
 * @param <P> the player variant
 * @param <L> the playerless variant
 */
public class PlayerPlayerlessAdapter<P, L> {
    /**
     * Instruction used to create the types.
     */
    protected final Instruction instruction;

    /**
     * Player variant.
     */
    @Nullable
    protected final P player;

    /**
     * Playerless variant.
     */
    @Nullable
    protected final L playerless;

    /**
     * Create a new Wrapper with instruction and at least one type.
     *
     * @param instruction the instruction used to create the types
     * @param player      the type requiring a profile for execution
     * @param playerless  the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     */
    public PlayerPlayerlessAdapter(final Instruction instruction, @Nullable final P player, @Nullable final L playerless) {
        if (player == null && playerless == null) {
            throw new IllegalStateException("Cannot create a Wrapper without a value!");
        }
        this.instruction = instruction;
        this.player = player;
        this.playerless = playerless;
    }

    /**
     * If it allows playerless it can be used with null profiles.
     *
     * @return if a playerless implementation is present
     */
    public boolean allowsPlayerless() {
        return playerless != null;
    }

    /**
     * Get the instruction of the types.
     *
     * @return the instruction used to create the types
     */
    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return instruction.toString();
    }
}
