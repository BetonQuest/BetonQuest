/**
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

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * Skript condition, which checks specified BetonQuest's condition
 * 
 * @author Jakub Sapalski
 */
public class SkriptConditionBQ extends Condition {

	private Expression<Player> player;
	private Expression<String> condition;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) arg0[0];
		condition = (Expression<String>) arg0[1];
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return player.getSingle(e).getName() + " meets " + condition.toString();
	}

	@Override
	public boolean check(Event e) {
		return BetonQuest.condition(PlayerConverter.getID(player.getSingle(e)), condition.getSingle(e));
	}

}
