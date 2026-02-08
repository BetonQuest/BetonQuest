package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for player and playerless placeholders.
 */
public class PlaceholderAdapter extends QuestAdapter<PlayerPlaceholder, PlayerlessPlaceholder> implements Argument<String>, PrimaryThreadEnforceable {

    /**
     * Instruction used to create the types.
     */
    private final Instruction instruction;

    /**
     * Create a new Wrapper for placeholders with instruction.
     *
     * @param instruction the instruction used to create the types
     * @param player      the type requiring a profile for execution
     * @param playerless  the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     */
    public PlaceholderAdapter(final Instruction instruction, @Nullable final PlayerPlaceholder player, @Nullable final PlayerlessPlaceholder playerless) {
        super(instruction.getPackage(), player, playerless);
        this.instruction = instruction;
    }

    /**
     * Resolves the placeholder for specified player.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this placeholder for given profile
     * @throws QuestException if the placeholder could not be resolved or requires a profile to resolve
     */
    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            if (playerless == null) {
                throw new QuestException("Non-static placeholder '" + instruction + "' cannot be retrieved without a profile reference!");
            }
            return playerless.getValue();
        }
        return player.getValue(profile);
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return player != null && player.isPrimaryThreadEnforced() || playerless != null && playerless.isPrimaryThreadEnforced();
    }
}
