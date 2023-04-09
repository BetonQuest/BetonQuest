package org.betonquest.betonquest.compatibility.quests;

import me.blackvein.quests.CustomReward;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Fires a BetonQuest event as a quest reward.
 */
@SuppressWarnings("PMD.CommentRequired")
public class EventReward extends CustomReward {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(EventReward.class);

    public EventReward() {
        super();
        setName("BetonQuest event");
        setAuthor("Co0sh");
        setRewardName("Event");
        addStringPrompt("Event", "Specify BetonQuest event name (with the package, like: package.event)", null);
    }

    @Override
    public void giveReward(final Player player, final Map<String, Object> dataMap) {
        final String string = dataMap.get("Event").toString();
        try {
            final OnlineProfile onlineProfile = PlayerConverter.getID(player);
            final EventID event = new EventID(null, string);
            BetonQuest.event(onlineProfile, event);
        } catch (final ObjectNotFoundException e) {
            LOG.warn("Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage(), e);
        }
    }

}
