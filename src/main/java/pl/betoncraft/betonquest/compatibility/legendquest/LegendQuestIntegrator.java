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
package pl.betoncraft.betonquest.compatibility.legendquest;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class LegendQuestIntegrator implements Integrator {

    private BetonQuest plugin;

    public LegendQuestIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("lqclass", LQClassCondition.class);
        plugin.registerConditions("lqrace", LQRaceCondition.class);
        plugin.registerConditions("lqattribute", LQAttributeCondition.class);
        plugin.registerConditions("lqkarma", LQKarmaCondition.class);
        plugin.registerVariable("lqclass", LQClassVariable.class);
        plugin.registerVariable("lqrace", LQRaceVariable.class);
        plugin.registerVariable("lqattribute", LQAttributeVariable.class);
        plugin.registerVariable("lqkarma", LQKarmaVariable.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
