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
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * Skript effect, which fires specified BetonQuest's event
 * 
 * @author Jakub Sapalski
 */
public class SkriptEffectBQ extends Effect {

	private Expression<String> event;
	private Expression<Player> player;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.event = (Expression<String>) exprs[0];
		this.player = (Expression<Player>) exprs[1];
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "fire " + event.toString() + " for " + player.getSingle(e).getName();
	}

	@Override
	protected void execute(final Event e) {
		new BukkitRunnable() {
			public void run() {
				BetonQuest.event(PlayerConverter.getID(player.getSingle(e)), event.getSingle(e));
			}
		}.runTask(BetonQuest.getInstance());
	}

}
