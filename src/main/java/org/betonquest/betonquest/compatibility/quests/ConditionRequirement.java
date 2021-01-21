package org.betonquest.betonquest.compatibility.quests;

import me.blackvein.quests.CustomRequirement;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;

/**
 * Requires the player to meet specified condition.
 */
@SuppressWarnings("PMD.CommentRequired")
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
        } catch (ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while checking quest requirement - BetonQuest condition '" + string + "' not found: " + e.getMessage());
            LogUtils.logThrowable(e);
            return false;
        }
    }

}
