package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Provides information about a Player's Location.
 * <p>
 * Format:
 * {@code %location.<mode>.<precision>%}
 * <p>
 * Modes:<br>
 * * xyz - The x, y and z location of the npc, separated by spaces<br>
 * * x - The x location of the npc<br>
 * * y - The y location of the npc<br>
 * * z - The z location of the npc<br>
 * * world - The world location of the npc<br>
 * * yaw - The yaw of the npc<br>
 * * pitch - The pitch of the npc<br>
 * * ulfShort - The location of the npc in the form x;y;z;world<br>
 * * ulfLong - The location of the npc in the form x;y;z;world;yaw;pitch<br>
 * Precision is decimals of precision desired, defaults to 0.<br>
 */
public class LocationVariable extends Variable {
    /**
     * The mode of the location response required. Provides multiple output formats.
     *
     * @see MODE
     */
    private final MODE mode;

    /**
     * The decimals of precision required, defaults to 0.
     */
    private final int decimalPlaces;

    /**
     * Construct a new LocationVariable that allows for resolution of information about a Player's Location.
     *
     * @param instruction The Instruction.
     * @throws QuestException If there was an error parsing the Instruction.
     */
    public LocationVariable(final Instruction instruction) throws QuestException {
        super(instruction);

        if (instruction.hasNext()) {
            mode = MODE.getMode(instruction.next());
        } else {
            mode = MODE.ULF_LONG;
        }

        if (instruction.hasNext()) {
            decimalPlaces = instruction.getInt();
        } else {
            decimalPlaces = 0;
        }
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        return profile.getOnlineProfile()
                .map(onlineProfile -> getForLocation(onlineProfile.getPlayer().getLocation()))
                .orElse("");
    }

    /**
     * Gets the location for the given mode.
     *
     * @param location The location to get the value for.
     * @return The value for the given location.
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public String getForLocation(final Location location) {
        return switch (mode) {
            case XYZ -> buildFormattedLocation(location, buildPart(1) + " " + buildPart(2) + " " + buildPart(3));
            case X -> buildFormattedLocation(location, buildPart(1));
            case Y -> buildFormattedLocation(location, buildPart(2));
            case Z -> buildFormattedLocation(location, buildPart(3));
            case WORLD -> buildFormattedLocation(location, "%4$s");
            case YAW -> buildFormattedLocation(location, buildPart(5));
            case PITCH -> buildFormattedLocation(location, buildPart(6));
            case ULF_SHORT ->
                    buildFormattedLocation(location, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s");
            case ULF_LONG ->
                    buildFormattedLocation(location, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s" + ";" + buildPart(5) + ";" + buildPart(6));
        };
    }

    private String buildFormattedLocation(final Location playerLocation, final String format) {
        final double posX = playerLocation.getX();
        final double posY = playerLocation.getY();
        final double posZ = playerLocation.getZ();
        final float yaw = playerLocation.getYaw();
        final float pitch = playerLocation.getPitch();
        final String world = playerLocation.getWorld().getName();

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

    private String buildPart(final int index) {
        if (decimalPlaces == 0) {
            return "%" + index + "$s";
        } else {
            return "%" + index + "$." + decimalPlaces + "f";
        }
    }

    /**
     * The mode of data required from the LocationVariable for the Player.
     */
    private enum MODE {
        /**
         * The x, y and z location of the player, separated by spaces
         */
        XYZ("xyz"),

        /**
         * The x location of the player
         */
        @SuppressWarnings("PMD.ShortVariable")
        X("x"),

        /**
         * The y location of the player
         */
        @SuppressWarnings("PMD.ShortVariable")
        Y("y"),

        /**
         * The z location of the player
         */
        @SuppressWarnings("PMD.ShortVariable")
        Z("z"),

        /**
         * The world location of the player
         */
        WORLD("world"),

        /**
         * The yaw of the player
         */
        YAW("yaw"),

        /**
         * The pitch of the player
         */
        PITCH("pitch"),

        /**
         * The location of the player in the form x;y;z;world
         */
        ULF_SHORT("ulfShort"),

        /**
         * The location of the player in the form x;y;z;world;yaw;pitch
         */
        ULF_LONG("ulfLong");

        /**
         * The name of the Mode.
         */
        private final String name;

        MODE(final String name) {
            this.name = name;
        }

        /**
         * Get the Mode corresponding to the specified String.
         *
         * @param mode The mode as a String.
         * @return A Mode.
         * @throws QuestException If there is an error parsing the mode String.
         */
        public static MODE getMode(final String mode) throws QuestException {
            for (final MODE targetMode : values()) {
                if (targetMode.name.equalsIgnoreCase(mode)) {
                    return targetMode;
                }
            }
            throw new QuestException("Unknown LocationVariable mode '" + mode + "'!");
        }
    }
}
