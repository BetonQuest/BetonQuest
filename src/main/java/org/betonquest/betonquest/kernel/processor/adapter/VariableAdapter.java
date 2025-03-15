package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for player and playerless variables.
 */
public class VariableAdapter extends PlayerPlayerlessAdapter<PlayerVariable, PlayerlessVariable> {

    /**
     * Create a new Wrapper for variables with instruction.
     *
     * @param instruction the instruction used to create the types
     * @param player      the type requiring a profile for execution
     * @param playerless  the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     */
    public VariableAdapter(final Instruction instruction, @Nullable final PlayerVariable player, @Nullable final PlayerlessVariable playerless) {
        super(instruction, player, playerless);
    }

    /**
     * Resolves the variable for specified player.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this variable for given profile
     * @throws QuestException if the variable could not be resolved or requires a profile to resolve
     */
    public String getValue(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            if (playerless == null) {
                throw new QuestException("Non-static variable '" + instruction + "' cannot be retrieved without a profile reference!");
            }
            return playerless.getValue();
        }
        return player.getValue(profile);
    }
}
