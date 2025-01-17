package org.betonquest.betonquest.quest.variable.location;

import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.Location;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.function.BiFunction;

/**
 * The mode of the location response required. Provides multiple output formats.
 */
public enum LocationFormationMode {

    /**
     * The x, y and z location of the player, separated by spaces.
     */
    XYZ("xyz", (location, decimalPlaces) -> {
        final String format = buildPart(1, decimalPlaces) + " " + buildPart(2, decimalPlaces) + " "
                + buildPart(3, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The x location of the player.
     */
    @SuppressWarnings("PMD.ShortVariable")
    X("x", (location, decimalPlaces) -> {
        final String format = buildPart(1, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The y location of the player.
     */
    @SuppressWarnings("PMD.ShortVariable")
    Y("y", (location, decimalPlaces) -> {
        final String format = buildPart(2, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The z location of the player.
     */
    @SuppressWarnings("PMD.ShortVariable")
    Z("z", (location, decimalPlaces) -> {
        final String format = buildPart(3, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The world location of the player.
     */
    WORLD("world", (location, decimalPlaces) -> {
        final String format = "%4$s";
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The yaw of the player.
     */
    YAW("yaw", (location, decimalPlaces) -> {
        final String format = buildPart(5, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The pitch of the player.
     */
    PITCH("pitch", (location, decimalPlaces) -> {
        final String format = buildPart(6, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The location of the player in the form x;y;z;world.
     */
    ULF_SHORT("ulfShort", (location, decimalPlaces) -> {
        final String format = buildPart(1, decimalPlaces) + ";" + buildPart(2, decimalPlaces) + ";"
                + buildPart(3, decimalPlaces) + ";" + "%4$s";
        return buildFormattedLocation(location, format, decimalPlaces);
    }),

    /**
     * The location of the player in the form x;y;z;world;yaw;pitch.
     */
    ULF_LONG("ulfLong", (location, decimalPlaces) -> {
        final String format = buildPart(1, decimalPlaces) + ";" + buildPart(2, decimalPlaces) + ";"
                + buildPart(3, decimalPlaces) + ";" + "%4$s" + ";" + buildPart(5, decimalPlaces) + ";"
                + buildPart(6, decimalPlaces);
        return buildFormattedLocation(location, format, decimalPlaces);
    });

    /**
     * The name of the Mode.
     */
    private final String name;

    /**
     * The formatter function for the mode.
     */
    private final BiFunction<Location, Integer, String> formatter;

    LocationFormationMode(final String name, final BiFunction<Location, Integer, String> formatter) {
        this.name = name;
        this.formatter = formatter;
    }

    /**
     * Get the Mode corresponding to the specified String.
     *
     * @param mode The mode as a String.
     * @return A Mode.
     * @throws QuestException If there is an error parsing the mode String.
     */
    public static LocationFormationMode getMode(final String mode) throws QuestException {
        for (final LocationFormationMode targetMode : values()) {
            if (targetMode.name.equalsIgnoreCase(mode)) {
                return targetMode;
            }
        }
        throw new QuestException("Unknown LocationVariable mode '" + mode + "'!");
    }

    private static String buildFormattedLocation(final Location location, final String format, final int decimalPlaces) {
        final double posX = location.getX();
        final double posY = location.getY();
        final double posZ = location.getZ();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();
        final String world = location.getWorld().getName();

        if (decimalPlaces == 0) {
            final DecimalFormat formatter = new DecimalFormat("#");
            formatter.setRoundingMode(RoundingMode.FLOOR);
            return String.format(Locale.US, format,
                    formatter.format(posX),
                    formatter.format(posY),
                    formatter.format(posZ),
                    world,
                    formatter.format(yaw),
                    formatter.format(pitch));
        } else {
            return String.format(Locale.US, format, posX, posY, posZ, world, yaw, pitch);
        }
    }

    private static String buildPart(final int index, final int decimalPlaces) {
        if (decimalPlaces == 0) {
            return "%" + index + "$s";
        } else {
            return "%" + index + "$." + decimalPlaces + "f";
        }
    }

    /**
     * Get the formatted location String of the specified location.
     *
     * @param location The location to format.
     * @param decimal  The number of decimal places to round to.
     * @return The formatted location String
     */
    public String getFormattedLocation(final Location location, final int decimal) {
        return formatter.apply(location, decimal);
    }
}
