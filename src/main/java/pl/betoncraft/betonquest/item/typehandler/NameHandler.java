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
package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;

public class NameHandler {

    private String name = null;
    private Existence existence = Existence.WHATEVER;

    public NameHandler() {}

    public void set(final String name) throws InstructionParseException {
        if (name == null || name.isEmpty()) {
            throw new InstructionParseException("Name cannot be empty");
        }
        if (name.equalsIgnoreCase("none")) {
            existence = Existence.FORBIDDEN;
        } else {
            this.name = name.replace('_', ' ').replace('&', '§');
            existence = Existence.REQUIRED;
        }
    }

    public String get() {
        return name;
    }

    public boolean check(final String name) {
        switch (existence) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return this.name.equals(name);
            case FORBIDDEN:
                return name == null;
            default:
                return false;
        }
    }

}
