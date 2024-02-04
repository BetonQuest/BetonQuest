package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.module.BukkitCustomRequirement;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

/**
 * Requires the player to meet specified condition.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ConditionRequirement extends BukkitCustomRequirement {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    public ConditionRequirement(final BetonQuestLogger log) {
        super();
        this.log = log;
        setName("BetonQuest condition");
        setAuthor("BetonQuest");
        addStringPrompt("Condition", "Specify BetonQuest condition name (with the package, like: package.condition)", null);
    }

    @Override
    public boolean testRequirement(final UUID uuid, final Map<String, Object> dataMap) {
        final String string = dataMap.get("Condition").toString();
        try {
            final OnlineProfile onlineProfile = PlayerConverter.getID(Bukkit.getPlayer(uuid));
            final ConditionID condition = new ConditionID(null, string);
            return BetonQuest.condition(onlineProfile, condition);
        } catch (final ObjectNotFoundException e) {
            log.warn("Error while checking quest requirement - BetonQuest condition '" + string + "' not found: " + e.getMessage(), e);
            return false;
        }
    }

}
