package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomReward;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
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
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create a new 'Quests' Reward.
     *
     * @param log             the custom logger
     * @param placeholders    the {@link Placeholders} to create and resolve placeholders
     * @param packManager     the quest package manager to get quest packages from
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public EventReward(final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager, final QuestTypeApi questTypeApi,
                       final ProfileProvider profileProvider) {
        super();
        this.log = log;
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        setName("BetonQuest event");
        setAuthor("BetonQuest");
        addStringPrompt("Event", "Specify BetonQuest event with the package and the name", null);
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
            final ActionID event = new ActionID(placeholders, packManager, null, string);
            questTypeApi.event(profileProvider.getProfile(player), event);
        } catch (final QuestException e) {
            log.warn("Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage(), e);
        }
    }
}
