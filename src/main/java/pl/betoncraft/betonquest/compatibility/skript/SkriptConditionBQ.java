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

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Skript condition, which checks specified BetonQuest's condition
 *
 * @author Jakub Sapalski
 */
public class SkriptConditionBQ extends Condition {

    private Expression<Player> player;
    private Expression<String> condition;

    public SkriptConditionBQ() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] arg0, final int arg1, final Kleenean arg2, final ParseResult arg3) {
        player = (Expression<Player>) arg0[0];
        condition = (Expression<String>) arg0[1];
        return true;
    }

    @Override
    public String toString(final Event event, final boolean debug) {
        return player.getSingle(event).getName() + " meets " + condition.toString();
    }

    @Override
    public boolean check(final Event event) {
        final String conditionID = condition.getSingle(event);
        try {
            return BetonQuest.condition(PlayerConverter.getID(player.getSingle(event)), new ConditionID(null, conditionID));
        } catch (ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while checking Skript condition - could not load condition with ID '" + conditionID + "': " + e.getMessage());
            LogUtils.logThrowable(e);
            return false;
        }
    }

}
