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

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import org.bukkit.event.Event;

/**
 * Skript event, which listens to custom event fired by BetonQuest's event
 *
 * @author Coosh
 */
public class SkriptEventBQ extends SkriptEvent {

    private Literal<?> literal;

    @Override
    public String toString(Event e, boolean debug) {
        return "on betonquest event";
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        literal = args[0];
        return true;
    }

    @Override
    public boolean check(Event e) {
        if (e instanceof BQEventSkript.CustomEventForSkript) {
            final BQEventSkript.CustomEventForSkript event = (BQEventSkript.CustomEventForSkript) e;
            return literal.check(e, new Checker<Object>() {
                @Override
                public boolean check(Object o) {
                    if (o instanceof String) {
                        String id = (String) o;
                        return (event.getID().equals(id));
                    }
                    return false;
                }

            });
        }
        return false;
    }

}
