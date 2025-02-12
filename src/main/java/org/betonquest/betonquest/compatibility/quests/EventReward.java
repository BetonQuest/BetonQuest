package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomReward;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Fires a BetonQuest event as a quest reward.
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class EventReward extends BukkitCustomReward {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new 'Quests' Reward.
     *
     * @param log          the custom logger
     * @param questTypeAPI the Quest Type API
     */
    public EventReward(final BetonQuestLogger log, final QuestTypeAPI questTypeAPI) {
        super();
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        setName("BetonQuest event");
        setAuthor("BetonQuest");
        addStringPrompt("Event", "Specify BetonQuest event name (with the package, like: package.event)", null);
    }

    @Override
    public void giveReward(final UUID uuid, final Map<String, Object> dataMap) {
        final Object object = dataMap.get("Event");
        if (object == null) {
            log.warn("Error while checking quest requirement - Missing Event Object");
            return;
        }
        final String string = object.toString();
        try {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                log.warn("Error while running quest reward - Player with UUID '" + uuid + "' not found.");
                return;
            }
            final OnlineProfile onlineProfile = BetonQuest.getInstance().getProfileProvider().getProfile(player);
            final EventID event = new EventID(null, string);
            questTypeAPI.event(onlineProfile, event);
        } catch (final QuestException e) {
            log.warn("Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage(), e);
        }
    }
}
