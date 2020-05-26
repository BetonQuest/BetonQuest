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

public class HeadOwnerHandler {

    private String owner = null;
    private Existence ownerE = Existence.WHATEVER;

    public void set(String string) {
        if (string.equalsIgnoreCase("none")) {
            ownerE = Existence.FORBIDDEN;
        } else {
            owner = string;
            ownerE = Existence.REQUIRED;
        }
    }

    public String get() {
        return owner;
    }

    public boolean check(String string) {
        switch (ownerE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(owner);
            case FORBIDDEN:
                return string == null;
            default:
                return false;
        }
    }

}
