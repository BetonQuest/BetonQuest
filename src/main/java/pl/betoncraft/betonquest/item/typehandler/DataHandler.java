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
import pl.betoncraft.betonquest.item.QuestItem.Number;

public class DataHandler {

    private short data = 0;
    private Number number = Number.WHATEVER;

    private DataHandler() {}

    public void set(String data) throws InstructionParseException {
        if (data.endsWith("-")) {
            number = Number.LESS;
            data = data.substring(0, data.length() - 1);
        } else if (data.endsWith("+")) {
            number = Number.MORE;
            data = data.substring(0, data.length() - 1);
        } else {
            number = Number.EQUAL;
        }
        try {
            this.data = Short.valueOf(data);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse item data value", e);
        }
//		if (this.data < 0) {
//			throw new InstructionParseException("Item data value cannot be negative");
//		}
    }

    public short get() {
        return data;
    }

    public boolean check(final int data) {
        switch (number) {
            case WHATEVER:
                return true;
            case EQUAL:
                return this.data == data;
            case MORE:
                return this.data <= data;
            case LESS:
                return this.data >= data;
            default:
                return false;
        }
    }

}
