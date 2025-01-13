package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomRequirement;
import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
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
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Create a new 'Quests' Condition Requirement.
     *
     * @param log      the custom logger
     * @param questAPI the BetonQuest API
     */
    public ConditionRequirement(final BetonQuestLogger log, final BetonQuestAPI questAPI) {
        super();
        this.log = log;
        this.questAPI = questAPI;
        setName("BetonQuest condition");
        setAuthor("BetonQuest");
        addStringPrompt("Condition", "Specify BetonQuest condition name (with the package, like: package.condition)", null);
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
            final OnlineProfile onlineProfile = PlayerConverter.getID(player);
            final ConditionID condition = new ConditionID(null, string);
            return questAPI.condition(onlineProfile, condition);
        } catch (final ObjectNotFoundException e) {
            log.warn("Error while checking quest requirement - BetonQuest condition '" + string + "' not found: " + e.getMessage(), e);
            return false;
        }
    }
}
