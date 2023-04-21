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
 * @see org.betonquest.betonquest.variables.LocationVariable
 */
public class CitizensVariable extends Variable {
    /**
     * The minimum number of required arguments when using a CitizensVariable through an Instruction.
     */
    private static final int MINIMUM_INSTRUCTION_ARGUMENTS = 3;

    /**
     * The integer ID of the NPC.
     */
    private final int npcId;

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private final ARGUMENT key;

    /**
     * A wrapper for the location property of the NPC.
     */
    private final LocationVariable location;

    /**
     * Construct a new CitizensVariable that allows for resolution of information about a Citizens NPC.
     * @param instruction The Instruction.
     * @throws InstructionParseException If there was an error parsing the Instruction.
     */
    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        try {
            final String[] splitInstruction = instruction.getInstruction().split("\\.");
            if (splitInstruction.length < MINIMUM_INSTRUCTION_ARGUMENTS) {
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

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private enum ARGUMENT {
        /**
         * Retrieve the name of the NPC.
         */
        NAME,

        /**
         * Retrieve the full name of the NPC.
         */
        FULL_NAME,

        /**
         * Retrieve the location of the NPC.
         */
        LOCATION
    }

}
