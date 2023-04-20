package org.betonquest.betonquest.compatibility.citizens;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.variables.LocationVariable;

import java.util.Arrays;
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

    private final LocationVariable location;

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

            final String newInstruction = String.join(".", Arrays.copyOfRange(splitInstruction, 2, splitInstruction.length));
            location = new LocationVariable(new Instruction(instruction.getPackage(), null, newInstruction));
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

        return switch (key) {
            case NAME -> npc.getName();
            case FULL_NAME -> npc.getFullName();
            case LOCATION -> location.getForLocation(npc.getStoredLocation());
        };
    }

    private enum ARGUMENT {
        NAME,
        FULL_NAME,
        LOCATION
    }

}
