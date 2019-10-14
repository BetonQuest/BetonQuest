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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.utils.Utils;

public class ColorHandler {

    private Color color = Bukkit.getServer().getItemFactory().getDefaultLeatherColor();
    private Existence colorE = Existence.WHATEVER;

    public void set(String string) throws InstructionParseException {
        if (string.equalsIgnoreCase("none")) {
            colorE = Existence.FORBIDDEN;
            return;
        }
        color = Utils.getColor(string);
        colorE = Existence.REQUIRED;
    }

    public Color get() {
        return color;
    }

    public boolean check(Color color) {
        switch (colorE) {
            case WHATEVER:
                return true;
            case REQUIRED:
            case FORBIDDEN: // if it's forbidden, this.color is default leather color (undyed)
                return color.equals(this.color);
            default:
                return false;
        }
    }
}
