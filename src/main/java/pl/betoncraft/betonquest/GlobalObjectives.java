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
package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.id.ObjectiveID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for global objectives
 *
 * @author Jonas Blocher
 */
public class GlobalObjectives {

    private static GlobalObjectives instance;

    private Set<ObjectiveID> globalObjectives;

    public GlobalObjectives() {
        instance = this;
        globalObjectives = new HashSet<>();

    }

    /**
     * Adds a objective to the list of global objectives.
     * This method should only be called in the constructor of global objectives
     */
    public static void add(final ObjectiveID objectiveID) {
        instance.globalObjectives.add(objectiveID);
    }

    /**
     * Starts all unstarted global objectives for the player
     *
     * @param playerID the id of the player
     */
    public static void startAll(final String playerID) {
        final PlayerData data = BetonQuest.getInstance().getPlayerData(playerID);
        for (final ObjectiveID id : instance.globalObjectives) {
            final Objective objective = BetonQuest.getInstance().getObjective(id);

            if (objective == null) {
                continue;
            }

            //if player already has the tag skip
            if (data.hasTag(GlobalObjectives.getTag(id))) {
                continue;
            }
            //start the objective
            objective.newPlayer(playerID);
            //add the tag
            data.addTag(GlobalObjectives.getTag(id));
        }
    }

    /**
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final ObjectiveID objectiveID) {
        return objectiveID.getPackage().getName() + ".global-" + objectiveID.getBaseID();
    }

    /**
     * @return a list of all loaded global objectives
     */
    public static List<ObjectiveID> list() {
        return new ArrayList<>(instance.globalObjectives);
    }
}
