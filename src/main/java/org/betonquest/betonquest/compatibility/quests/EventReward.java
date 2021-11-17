package org.betonquest.betonquest.compatibility.quests;

import lombok.CustomLog;
import me.blackvein.quests.CustomReward;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Fires a BetonQuest event as a quest reward.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class EventReward extends CustomReward {

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
            final String playerID = PlayerConverter.getID(player);
            final EventID event = new EventID(null, string);
            BetonQuest.event(playerID, event);
        } catch (final ObjectNotFoundException e) {
            LOG.warning("Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage(), e);
        }
    }

}
