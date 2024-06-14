package org.betonquest.betonquest.quest.condition.advancement;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;

/**
 * Checks if the player has specified advancement.
 */
public class AdvancementCondition implements PlayerCondition {
    /**
     * Advancement which is required.
     */
    private final Advancement advancement;

    /**
     * Create a new Advancement condition.
     *
     * @param advancement the required advancement
     */
    public AdvancementCondition(final Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        final AdvancementProgress progress = profile.getOnlineProfile().get().getPlayer().getAdvancementProgress(advancement);
        return progress.isDone();
    }
}
