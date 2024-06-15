package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
     * Create a legacy condition from an {@link Event} and a {@link PlayerlessCondition}. If the condition does not support
     * playerless execution ({@code staticness = false}) then no {@link PlayerlessCondition} instance must be provided.
     * <p>
     * When no normal condition is given the playerless condition is required.
     *
     * @param instruction         instruction used to create the conditions
     * @param playerCondition     condition to use
     * @param playerlessCondition playerless condition to use or null if no playerless checking is supported
     */
    public LegacyConditionAdapter(final Instruction instruction, @Nullable final PlayerCondition playerCondition,
                                  @Nullable final PlayerlessCondition playerlessCondition) {
        super(instruction, false);
        if (playerCondition == null && playerlessCondition == null) {
            throw new IllegalArgumentException("Either the normal or playerless factory must be present!");
        }
        this.playerCondition = playerCondition;
        this.playerlessCondition = playerlessCondition;
        staticness = playerlessCondition != null;
        persistent = true;
    }

    @Override
    protected Boolean execute(@Nullable final Profile profile) throws QuestRuntimeException {
        if (playerCondition == null || profile == null) {
            Objects.requireNonNull(playerlessCondition);
            return playerlessCondition.check();
        } else {
            final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
            if (onlineProfile.isPresent()) {
                return playerCondition.check(onlineProfile.get());
            }
            return playerCondition.check(profile);
        }
    }
}
