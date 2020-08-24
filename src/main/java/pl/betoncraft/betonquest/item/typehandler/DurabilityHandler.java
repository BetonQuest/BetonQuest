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

public class DurabilityHandler {

    private short durability = 0;
    private Number number = Number.WHATEVER;

    public DurabilityHandler() {}

    public void set(String durability) throws InstructionParseException {
        if (durability.endsWith("-")) {
            number = Number.LESS;
            durability = durability.substring(0, durability.length() - 1);
        } else if (durability.endsWith("+")) {
            number = Number.MORE;
            durability = durability.substring(0, durability.length() - 1);
        } else {
            number = Number.EQUAL;
        }
        try {
            this.durability = Short.valueOf(durability);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse item durability value", e);
        }
    }

    public short get() {
        return durability;
    }

    public boolean check(final int durability) {
        switch (number) {
            case WHATEVER:
                return true;
            case EQUAL:
                return this.durability == durability;
            case MORE:
                return this.durability <= durability;
            case LESS:
                return this.durability >= durability;
            default:
                return false;
        }
    }

    /**
     * @return checks if the state of this type handler should be ignored
     */
    public boolean whatever() {
        return number == Number.WHATEVER;
    }

}
