package org.betonquest.betonquest.compatibility.quests;

import lombok.CustomLog;
import me.blackvein.quests.CustomRequirement;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Requires the player to meet specified condition.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class ConditionRequirement extends CustomRequirement {

    public ConditionRequirement() {
        super();
        setName("BetonQuest condition");
        setAuthor("Co0sh");
        addStringPrompt("Condition", "Specify BetonQuest condition name (with the package, like: package.condition)", null);
    }

    @Override
    public boolean testRequirement(final Player player, final Map<String, Object> dataMap) {
        final String string = dataMap.get("Condition").toString();
        try {
            final String playerID = PlayerConverter.getID(player);
            final ConditionID condition = new ConditionID(null, string);
            return BetonQuest.condition(playerID, condition);
        } catch (final ObjectNotFoundException e) {
            LOG.warning("Error while checking quest requirement - BetonQuest condition '" + string + "' not found: " + e.getMessage(), e);
            return false;
        }
    }

}
