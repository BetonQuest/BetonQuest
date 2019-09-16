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
package pl.betoncraft.betonquest.compatibility.skript;

import ch.njol.skript.Skript;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class SkriptIntegrator implements Integrator {

    private BetonQuest plugin;

    public SkriptIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        Skript.registerCondition(SkriptConditionBQ.class, "%player% (meet|meets) [betonquest] condition %string%");
        Skript.registerEffect(SkriptEffectBQ.class, "fire [betonquest] event %string% for %player%");
        Skript.registerEvent("betonquest", SkriptEventBQ.class, BQEventSkript.CustomEventForSkript.class,
                "[betonquest] event %string%");
        plugin.registerEvents("skript", BQEventSkript.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
