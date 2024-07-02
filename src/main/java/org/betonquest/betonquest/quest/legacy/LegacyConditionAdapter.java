package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.OnlinePlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Adapter for {@link PlayerCondition} and {@link PlayerlessCondition} to fit the old convention of
 * {@link org.betonquest.betonquest.api.Condition Legacy Condition}.
 */
public class LegacyConditionAdapter extends org.betonquest.betonquest.api.Condition {
    /**
     * The player condition to be adapted.
     */
    @Nullable
    private final PlayerCondition playerCondition;

    /**
     * The playerless condition to be adapted.
     */
    @Nullable
    private final PlayerlessCondition playerlessCondition;

    /**
     * The online player condition to be adapted.
     */
    @Nullable
    private final OnlinePlayerCondition onlinePlayerCondition;

    /**
     * Create a legacy condition from an {@link Event} and a {@link PlayerlessCondition}. If the condition does not support
     * playerless execution ({@code staticness = false}) then no {@link PlayerlessCondition} instance must be provided.
     * <p>
     * When no normal condition is given the playerless condition is required.
     *
     * @param instruction           instruction used to create the conditions
     * @param playerCondition       condition to use
     * @param playerlessCondition   playerless condition to use or null if no playerless checking is supported
     * @param onlinePlayerCondition online player condition to use
     * @throws IllegalArgumentException when no condition is given
     */
    public LegacyConditionAdapter(final Instruction instruction, @Nullable final PlayerCondition playerCondition,
                                  @Nullable final PlayerlessCondition playerlessCondition, @Nullable final OnlinePlayerCondition onlinePlayerCondition) {
        super(instruction, false);
        if (playerCondition == null && playerlessCondition == null && onlinePlayerCondition == null) {
            throw new IllegalArgumentException("At least one condition type must be present!");
        }
        this.playerCondition = playerCondition;
        this.playerlessCondition = playerlessCondition;
        this.onlinePlayerCondition = onlinePlayerCondition;
        staticness = playerlessCondition != null;
        persistent = playerCondition != null;
    }

    @Override
    protected Boolean execute(@Nullable final Profile profile) throws QuestRuntimeException {
        if (onlinePlayerCondition != null && profile != null) {
            final Optional<OnlineProfile> optional = profile.getOnlineProfile();
            if (optional.isPresent()) {
                return onlinePlayerCondition.check(optional.get());
            }
        }
        if (playerCondition != null && profile != null) {
            return playerCondition.check(profile);
        }
        if (playerlessCondition != null) {
            return playerlessCondition.check();
        }
        throw new QuestRuntimeException("Invalid profile for non-static condition!");
    }
}
