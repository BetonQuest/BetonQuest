package org.betonquest.betonquest.compatibility.citizens;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Location;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Provides information about a citizen npc.
 * <p>
 * Format:
 * {@code %citizen.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:
 * * name - Return citizen name
 * * full_name - Full Citizen name
 * * location - Return citizen location, defaults to ulfLong
 * Modes:
 * * xyz - The x, y and z location of the npc, separated by spaces
 * * x - The x location of the npc
 * * y - The y location of the npc
 * * z - The z location of the npc
 * * world - The world location of the npc
 * * yaw - The yaw of the npc
 * * pitch - The pitch of the npc
 * * ulfShort - The location of the npc in the form x;y;z;world
 * * ulfLong - The location of the npc in the form x;y;z;world;yaw;pitch
 * Precision is decimals of precision desired, defaults to 0.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensVariable extends Variable {
    private final int npcId;

    private final ARGUMENT key;
    private final MODE mode;
    private final int decimalPlaces;

    @SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        try {
            final String[] splitInstruction = instruction.getInstruction().split("\\.");
            if (splitInstruction.length < 3) {
                throw new InstructionParseException("Not enough arguments");
            } else {
                npcId = Integer.parseInt(splitInstruction[1]);
                key = ARGUMENT.valueOf(splitInstruction[2].toUpperCase(Locale.ROOT));
            }

            if (splitInstruction.length > 3) {
                mode = MODE.getMode(splitInstruction[3]);
            } else {
                mode = MODE.ULF_LONG;
            }

            if (splitInstruction.length > 4) {
                decimalPlaces = Integer.parseInt(splitInstruction[4]);
            } else {
                decimalPlaces = 0;
            }
        } catch (final IllegalArgumentException exception) {
            throw new InstructionParseException(exception);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public String getValue(final Profile profile) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            return "";
        }

        switch (key) {
            case NAME:
                return npc.getName();
            case FULL_NAME:
                return npc.getFullName();
            case LOCATION:
                final Location loc = npc.getStoredLocation();
                switch (mode) {
                    case XYZ:
                        return buildFormattedLocation(loc, buildPart(1) + " " + buildPart(2) + " " + buildPart(3));
                    case X:
                        return buildFormattedLocation(loc, buildPart(1));
                    case Y:
                        return buildFormattedLocation(loc, buildPart(2));
                    case Z:
                        return buildFormattedLocation(loc, buildPart(3));
                    case WORLD:
                        return buildFormattedLocation(loc, "%4$s");
                    case YAW:
                        return buildFormattedLocation(loc, buildPart(5));
                    case PITCH:
                        return buildFormattedLocation(loc, buildPart(6));
                    case ULF_SHORT:
                        return buildFormattedLocation(loc, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s");
                    case ULF_LONG:
                        return buildFormattedLocation(loc, buildPart(1) + ";" + buildPart(2) + ";" + buildPart(3) + ";" + "%4$s" + ";" + buildPart(5) + ";" + buildPart(6));
                }
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

    private enum ARGUMENT {
        NAME,
        FULL_NAME,
        LOCATION
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
