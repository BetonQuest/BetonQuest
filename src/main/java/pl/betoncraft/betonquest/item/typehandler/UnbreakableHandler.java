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

import pl.betoncraft.betonquest.item.QuestItem.Existence;

public class UnbreakableHandler {

    private Existence unbreakable = Existence.WHATEVER;

    public void set(String string) {
        if (string.equalsIgnoreCase("true")) {
            unbreakable = Existence.REQUIRED;
        } else {
            unbreakable = Existence.FORBIDDEN;
        }
    }

    public boolean get() {
        return unbreakable == Existence.REQUIRED;
    }

    public boolean check(boolean bool) {
        switch (unbreakable) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return bool;
            case FORBIDDEN:
                return !bool;
            default:
                return false;
        }
    }

}
