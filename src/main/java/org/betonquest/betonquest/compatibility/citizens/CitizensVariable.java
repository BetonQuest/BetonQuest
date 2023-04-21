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
 * Arguments:<br>
 * * name - Return citizen name<br>
 * * full_name - Full Citizen name<br>
 * * location - Return citizen location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 *
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
     *
     * @param instruction The Instruction.
     * @throws InstructionParseException If there was an error parsing the Instruction.
     */
    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        final String[] splitInstruction = instruction.getInstruction().split("\\.");
        if (splitInstruction.length < MINIMUM_INSTRUCTION_ARGUMENTS) {
            throw new InstructionParseException("Not enough arguments, must have at least " + MINIMUM_INSTRUCTION_ARGUMENTS);
        } else {
            try {
                npcId = Integer.parseInt(splitInstruction[1]);
                if (npcId < 0) {
                    throw new InstructionParseException("The specified NPC ID was not a positive or zero integer");
                }
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("The specified NPC ID was not a valid integer", e);
            }

            final String argument = splitInstruction[2].toUpperCase(Locale.ROOT);
            try {
                key = ARGUMENT.valueOf(argument);
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Specified CitizenVariable argument was not recognized: '" + argument + "'", e);
            }
        }

        final String newInstruction = String.join(".", Arrays.copyOfRange(splitInstruction, 2, splitInstruction.length));
        location = new LocationVariable(new Instruction(instruction.getPackage(), null, newInstruction));
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
