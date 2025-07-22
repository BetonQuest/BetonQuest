package org.betonquest.betonquest.quest.condition.advancement;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;

/**
 * Checks if the player has specified advancement.
 */
public class AdvancementCondition implements OnlineCondition {
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
    public boolean check(final OnlineProfile profile) throws QuestException {
        final AdvancementProgress progress = profile.getPlayer().getAdvancementProgress(advancement);
        return progress.isDone();
    }
}
