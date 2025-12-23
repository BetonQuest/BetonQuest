package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter for {@link PlayerCondition} and {@link PlayerlessCondition}.
 */
public class ConditionAdapter extends QuestAdapter<PlayerCondition, PlayerlessCondition> implements PrimaryThreadEnforceable {

    /**
     * Create a new Adapter with instruction and at least one type.
     *
     * @param pack       the package where the types are from
     * @param player     the type requiring a profile for execution
     * @param playerless the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     */
    public ConditionAdapter(final QuestPackage pack, @Nullable final PlayerCondition player, @Nullable final PlayerlessCondition playerless) {
        super(pack, player, playerless);
    }

    /**
     * Checks the condition for specified player.
     *
     * @param profile the {@link Profile} used for checking
     * @return f the condition is fulfilled
     * @throws QuestException if the condition could not be checked or requires a profile to check
     */
    public boolean check(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            if (playerless == null) {
                throw new QuestException("Non-static condition cannot be checked without a profile reference!");
            }
            return playerless.check();
        }
        return player.check(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return player != null && player.isPrimaryThreadEnforced() || playerless != null && playerless.isPrimaryThreadEnforced();
    }
}
