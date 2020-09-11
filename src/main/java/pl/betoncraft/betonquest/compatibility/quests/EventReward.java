package pl.betoncraft.betonquest.compatibility.quests;

import me.blackvein.quests.CustomReward;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Map;
import java.util.logging.Level;

/**
 * Fires a BetonQuest event as a quest reward.
 */
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
        } catch (ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

}
