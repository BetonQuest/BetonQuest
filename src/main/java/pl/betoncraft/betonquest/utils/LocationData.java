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
package pl.betoncraft.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.Locale;
import java.util.logging.Level;

/**
 * This class parses various location strings.
 */
public class LocationData {

    /**
     * This is a regex, that matches '123', '123.456', '0.456' and '.456'
     */
    public final static String REGEX_DOUBLE = "[+-]?([0-9]*[.])?[0-9]+";
    /**
     * This is a regex, that matches the format of a location
     */
    public final static String REGEX_LOCATION = REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";"
            + "((\\w|-)+\\.?)+" + "(;" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ")?";
    /**
     * This is a regex, that matches the format of a vector
     */
    public final static String REGEX_VECTOR = "\\(" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + "\\)";

    /**
     * If a {@link Location} is given, this represent it, otherwise null
     */
    private final Location loc;
    /**
     * If a {@link Variable} is given, this represent it, otherwise null
     */
    private final Variable variable;
    /**
     * The {@link Vector}
     */
    private final Vector vector;

    /**
     * This parse a string, that contains a {@link Location}. The location has
     * to be in the format '(x;y;z;world[;yaw;pitch])'. Optional it is followed
     * by the key '->', followed with a {@link Vector}. The vector has to be in
     * the format '(x;y;z)'. A {@link Vector} is added to the {@link Location}.
     *
     * @param packName     Name of the package, required for variable resolution
     * @param locationData string containing raw location, as defined above
     * @throws InstructionParseException Is thrown, if there is an error while parsing the
     *                                   locationData
     */
    public LocationData(final String packName, final String locationData) throws InstructionParseException {
        String base = null;
        if (locationData.contains("->")) {
            final String[] parts = locationData.split("->");
            vector = parseVector(parts[1]);
            base = parts[0];
        } else {
            vector = new Vector(0, 0, 0);
            base = locationData;
        }

        // TODO Remove this code in the version 1.13 or later
        // This support the old implementation of %player%
        // Don't forget to remove the Method
        base = resolvePlayerVariable(packName, base);

        if (base.startsWith("%") && base.endsWith("%")) {
            loc = null;
            variable = BetonQuest.createVariable(Config.getPackages().get(packName), base);
        } else {
            loc = parseLocation(base);
            variable = null;
        }
    }

    @Deprecated
    private String resolvePlayerVariable(final String pack, final String strg) {
        if ("player".equalsIgnoreCase(strg)) {
            LogUtils.getLogger().log(Level.WARNING, "You still use 'player' instead of '%location%' in package '" + pack
                    + "'. This is deprecated and should be updated. This will be removed in a future version.");
            return "%location%";
        }
        return strg;
    }

    private Location getBaseLoc(final String playerID) throws QuestRuntimeException {
        if (loc != null) {
            return loc;
        }
        if (variable != null) {
            if (playerID == null) {
                throw new QuestRuntimeException("Variable location cannot accessed without the player."
                        + " consider changing it to absolute coordinates");
            }
            final String value = variable.getValue(playerID);
            try {
                return parseLocation(value);
            } catch (InstructionParseException e) {
                throw new QuestRuntimeException("Error while parsing location.", e);
            }
        }
        return null;
    }

    /**
     * @param playerID ID of the player, needed for location resolution
     * @return the location represented by this object
     * @throws QuestRuntimeException Is thrown, when location is defined for the player but the
     *                               player cannot be accessed
     */
    public Location getLocation(final String playerID) throws QuestRuntimeException {
        return getBaseLoc(playerID).clone().add(vector);
    }

    /**
     * Parse the location from {@link LocationData#getLocation(String)} into a
     * String
     *
     * @param playerID the ID of the player if it is a Variable location, or null
     * @return The players location as a String.
     * @throws QuestRuntimeException
     */
    public String toString(final String playerID) throws QuestRuntimeException {
        final Location loc = getLocation(playerID);
        return String.format(Locale.US, "%.2f;%.2f;%.2f;%s;%.2f;%.2f", loc.getBlockX(), loc.getBlockY(),
                loc.getBlockZ(),
                loc.getWorld().getName(), loc.getYaw(), loc.getPitch());
    }

    /**
     * Parse a string into a location. The location has to be in the format
     * '(x;y;z;world[;yaw;pitch])'
     *
     * @param loc The string, that represent the location
     * @return The location
     * @throws InstructionParseException Is thrown, if the location is not in the right format, or it
     *                                   could not be parsed into double values, or the World does not
     *                                   exists
     */
    public static Location parseLocation(final String loc) throws InstructionParseException {
        if (loc == null || !loc.matches("^" + REGEX_LOCATION + "$")) {
            throw new InstructionParseException("Incorrect location format '" + loc
                    + "'. A location has to be in the format 'x;y;z;world[;yaw;pitch]'");
        }
        final String[] parts = loc.split(";");

        final World world = Bukkit.getWorld(parts[3]);
        if (world == null) {
            throw new InstructionParseException("World " + parts[3] + " does not exists.");
        }
        final double locX;
        final double locY;
        final double locZ;
        float yaw = 0;
        float pitch = 0;
        try {
            locX = Double.parseDouble(parts[0]);
            locY = Double.parseDouble(parts[1]);
            locZ = Double.parseDouble(parts[2]);
            if (parts.length == 6) {
                yaw = Float.parseFloat(parts[4]);
                pitch = Float.parseFloat(parts[5]);
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the location.", e);
        }
        return new Location(world, locX, locY, locZ, yaw, pitch);
    }

    /**
     * Parse a string into a vector. The Vector has to be in the format
     * '(x;y;z)'
     *
     * @param vector The string, that represent the vector
     * @return The vector
     * @throws InstructionParseException Is thrown, if the Vector is not in the right format, or it
     *                                   could not be parsed into double values
     */
    public static Vector parseVector(final String vector) throws InstructionParseException {
        if (vector == null || !vector.matches("^" + REGEX_VECTOR + "$")) {
            throw new InstructionParseException(
                    "Incorrect vector format '" + vector + "'. A vector has to be in the format '(x;y;z)'");
        }
        final String[] parts = vector.substring(1, vector.indexOf(')')).split(";");
        final double locX;
        final double locY;
        final double locZ;
        try {
            locX = Double.parseDouble(parts[0]);
            locY = Double.parseDouble(parts[1]);
            locZ = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the vector.", e);
        }
        return new Vector(locX, locY, locZ);
    }
}
