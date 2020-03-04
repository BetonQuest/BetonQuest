/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 *
 * @author Jakub Sapalski
 */
public class EventReward extends CustomReward {

    public EventReward() {
        setName("BetonQuest event");
        setAuthor("Co0sh");
        setRewardName("Event");
        addStringPrompt("Event", "Specify BetonQuest event name (with the package, like: package.event)", null);
    }

    @Override
    public void giveReward(Player player, Map<String, Object> dataMap) {
        String string = dataMap.get("Event").toString();
        try {
            String playerID = PlayerConverter.getID(player);
            EventID event = new EventID(null, string);
            BetonQuest.event(playerID, event);
        } catch (ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while running quest reward - BetonQuest event '" + string + "' not found: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

}
