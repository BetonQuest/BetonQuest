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
package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * ConditionID represents an condition
 */
public class ConditionID extends ID {

    /**
     * True if the condition is inverted with a exclamation mark
     */
    private final boolean inverted;

    /**
     * @see ID#ID(ConfigPackage, String)
     */
    public ConditionID(final ConfigPackage pack, final String id) throws ObjectNotFoundException {
        super(pack, removeExclamationMark(id));
        this.inverted = id.charAt(0) == '!';
    }

    @Override
    protected String generateRawInstruction() throws ObjectNotFoundException {
        final String rawInstruction = getPackage().getString("conditions." + getBaseID());
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Condition '" + getFullID() + "' is not defined!");
        }
        return rawInstruction;
    }

    public boolean isInverted() {
        return inverted;
    }

    /**
     * Invert a boolean, id the condition is inverted
     * 
     * @param bool
     *            that should be inverted
     * @return The inverted boolean, if the condiion is inverted
     */
    public boolean invert(final boolean bool) {
        return inverted ? !bool : bool;
    }

    private static String removeExclamationMark(final String id) {
        if (id.charAt(0) == '!') {
            return id.substring(1);
        }
        return id;
    }

}
