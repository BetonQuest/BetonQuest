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

import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class QuestsIntegrator implements Integrator {

    private static Quests questsInstance;
    private BetonQuest plugin;

    public QuestsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    public static Quests getQuestsInstance() {
        return questsInstance;
    }

    @Override
    public void hook() {
        questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        plugin.registerConditions("quest", QuestCondition.class);
        plugin.registerEvents("quest", QuestEvent.class);
        questsInstance.getCustomRewards().add(new EventReward());
        questsInstance.getCustomRequirements().add(new ConditionRequirement());
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
