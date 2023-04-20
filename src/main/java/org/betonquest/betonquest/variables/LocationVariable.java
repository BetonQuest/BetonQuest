package org.betonquest.betonquest.variables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
public class LocationVariable extends Variable {
    private final MODE mode;

    private final int decimalPlaces;

    public LocationVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        final String[] splitInstruction = instruction.getInstruction().split("\\.");
        if (splitInstruction.length >= 2) {
            mode = MODE.getMode(splitInstruction[1]);
        } else {
            mode = MODE.ULF_LONG;
        }

        if (splitInstruction.length >= 3) {
            try {
                decimalPlaces = Integer.parseInt(splitInstruction[2]);
            } catch (final NumberFormatException exception) {
                throw new InstructionParseException(exception);
            }
        } else {
            decimalPlaces = 0;
        }
    }

    @Override
    public String getValue(final Profile profile) {
        final Location playerLocation = profile.getOnlineProfile().get().getPlayer().getLocation();

        return getForLocation(playerLocation);
    }

    /**
     * Gets the location for the given mode.
     *
     * @param location The location to get the value for.
     * @return The value for the given location.
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    @NotNull
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

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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

    private enum MODE {
        XYZ("xyz"),
        X("x"),
        Y("y"),
        Z("z"),
        WORLD("world"),
        YAW("yaw"),
        PITCH("pitch"),
        ULF_SHORT("QuestPackage"),
        ULF_LONG("ulfLong");

        private final String name;

        MODE(final String name) {
            this.name = name;
        }

        public static MODE getMode(final String mode) throws InstructionParseException {
            for (final MODE targetMode : MODE.values()) {
                if (targetMode.name.equalsIgnoreCase(mode)) {
                    return targetMode;
                }
            }
            throw new InstructionParseException("Unknown LocationVariable mode '" + mode + "'!");
        }
    }
}
