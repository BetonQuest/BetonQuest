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

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Skript effect, which fires specified BetonQuest's event
 *
 * @author Jakub Sapalski
 */
public class SkriptEffectBQ extends Effect {

    private Expression<String> event;
    private Expression<Player> player;

    public SkriptEffectBQ() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
        this.event = (Expression<String>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    public String toString(final Event event, final boolean debug) {
        return "fire " + this.event.toString() + " for " + player.getSingle(event).getName();
    }

    @Override
    protected void execute(final Event event) {
        new BukkitRunnable() {
            public void run() {
                final String eventID = SkriptEffectBQ.this.event.getSingle(event);
                try {
                    BetonQuest.event(PlayerConverter.getID(player.getSingle(event)), new EventID(null, eventID));
                } catch (ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error when running Skript event - could not load '" + eventID + "' event: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }

            }
        }.runTask(BetonQuest.getInstance());
    }

}
