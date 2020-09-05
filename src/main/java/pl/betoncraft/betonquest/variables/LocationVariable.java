package pl.betoncraft.betonquest.variables;


import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Locale;

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
            } catch (NumberFormatException exception) {
                throw new InstructionParseException(exception);
            }
        } else {
            decimalPlaces = 0;
        }
    }

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

    private String buildFormattedLocation(final Location playerLocation, final String format) {
        return String.format(Locale.US, format,
                playerLocation.getX(),
                playerLocation.getY(),
                playerLocation.getZ(),
                playerLocation.getWorld().getName(),
                playerLocation.getYaw(),
                playerLocation.getPitch());
    }

    private String buildPart(final int index) {
        return "%" + index + "$." + decimalPlaces + "f";
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
