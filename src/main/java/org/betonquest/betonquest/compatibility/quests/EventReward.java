package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomReward;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

/**
 * Fires a BetonQuest event as a quest reward.
 */
@SuppressWarnings("PMD.CommentRequired")
public class EventReward extends BukkitCustomReward {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    public EventReward(final BetonQuestLogger log) {
        super();
        this.log = log;
        setName("BetonQuest event");
        setAuthor("BetonQuest");
        addStringPrompt("Event", "Specify BetonQuest event name (with the package, like: package.event)", null);
    }

    @Override
    public void giveReward(final UUID uuid, final Map<String, Object> dataMap) {
        final String string = dataMap.get("Event").toString();
        try {
            final OnlineProfile onlineProfile = PlayerConverter.getID(Bukkit.getPlayer(uuid));
            final EventID event = new EventID(null, string);
            BetonQuest.event(onlineProfile, event);
        } catch (final ObjectNotFoundException e) {
            log.warn("Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage(), e);
        }
    }

}
