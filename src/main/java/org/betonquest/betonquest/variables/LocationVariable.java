package org.betonquest.betonquest.variables;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Location;

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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public String getValue(final String playerID) {
        final Location playerLocation = PlayerConverter.getPlayer(playerID).getLocation();

        switch (mode) {
            case XYZ:
                return buildFormattedLocation(playerLocation, buildPart(1) + " " + buildPart(2) + " " + buildPart(3));
            case X:
                return buildFormattedLocation(playerLocation, buildPart(1));
            case Y:
                return buildFormattedLocation(playerLocation, buildPart(2));
            case Z:
                return buildFormattedLocation(playerLocation, buildPart(3));
            case WORLD:
                return buildFormattedLocation(playerLocation, "%4$s");
            case YAW:
                return buildFormattedLocation(playerLocation, buildPart(5));
            case PITCH:
                return buildFormattedLocation(playerLocation, buildPart(6));
            case ULF_SHORT:
                return buildFormattedLocation(playerLocation, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s");
            case ULF_LONG:
                return buildFormattedLocation(playerLocation, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s" + ";" + buildPart(5) + ";" + buildPart(6));
        }
        return "";
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
            formatter.setRoundingMode(RoundingMode.DOWN);
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
        ULF_SHORT("ulfShort"),
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
