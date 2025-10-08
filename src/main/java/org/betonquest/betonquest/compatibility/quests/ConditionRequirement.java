package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomRequirement;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Requires the player to meet specified condition.
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class ConditionRequirement extends BukkitCustomRequirement {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
     * Create a new 'Quests' Condition Requirement.
     *
     * @param log             the custom logger
     * @param packManager     the quest package manager to get quest packages from
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public ConditionRequirement(final BetonQuestLogger log, final QuestPackageManager packManager,
                                final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        super();
        this.log = log;
        this.packManager = packManager;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        setName("BetonQuest condition");
        setAuthor("BetonQuest");
        addStringPrompt("Condition", "Specify BetonQuest condition with the package and the name", null);
    }

    @Override
    public boolean testRequirement(final UUID uuid, final Map<String, Object> dataMap) {
        final Object object = dataMap.get("Condition");
        if (object == null) {
            log.warn("Error while checking quest requirement - Missing Condition Object");
            return false;
        }
        final String string = object.toString();
        try {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                log.warn("Error while running quest reward - Player with UUID '" + uuid + "' not found.");
                return false;
            }
            final ConditionID condition = new ConditionID(packManager, null, string);
            return questTypeApi.condition(profileProvider.getProfile(player), condition);
        } catch (final QuestException e) {
            log.warn("Error while checking quest requirement - BetonQuest condition '" + string + "' not found: " + e.getMessage(), e);
            return false;
        }
    }
}
