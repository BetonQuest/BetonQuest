package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;

/**
 * Event to update the visibility of all Npcs to one player now.
 */
public class UpdateVisibilityNowEvent implements OnlineEvent {
    /**
     * Npc Hider to update the visibility.
     */
    private final NpcHider npcHider;

    /**
     * Create a new update visibility event.
     *
     * @param npcHider the hider to update the visibility
     */
    public UpdateVisibilityNowEvent(final NpcHider npcHider) {
        this.npcHider = npcHider;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        npcHider.applyVisibility(profile);
    }
}
