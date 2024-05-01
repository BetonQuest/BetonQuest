package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiFunction;

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
    @Nullable
    private final LocationVariable location;

    /**
     * Construct a new CitizensVariable that allows for resolution of information about a Citizens NPC.
     *
     * @param instruction The Instruction.
     * @throws InstructionParseException If there was an error parsing the Instruction.
     */
    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("The specified NPC ID was not a positive or zero integer");
        }

        final String argument = instruction.next();
        try {
            key = ARGUMENT.valueOf(argument.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new InstructionParseException("Specified CitizenVariable argument was not recognized: '" + argument + "'", e);
        }

        if (key == ARGUMENT.LOCATION) {
            try {
                location = new LocationVariable(new Instruction(
                        BetonQuest.getInstance().getLoggerFactory().create(Instruction.class),
                        instruction.getPackage(),
                        new NoID(instruction.getPackage()),
                        "location." + String.join(".", instruction.getRemainingParts())
                ));
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Could not generate dynamic location variable", e);
            }
        } else {
            location = null;
        }
    }

    @Override
    public String getValue(final Profile profile) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            return "";
        }

        return key.resolve(npc, location);
    }

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private enum ARGUMENT {
        /**
         * Retrieve the name of the NPC.
         */
        NAME((npc, loc) -> npc.getName()),

        /**
         * Retrieve the full name of the NPC.
         */
        FULL_NAME((npc, loc) -> npc.getFullName()),

        /**
         * Retrieve the location of the NPC.
         */
        LOCATION((npc, loc) -> loc.getForLocation(npc.getStoredLocation()));

        /**
         * Function to resolve this argument from an NPC instance and optional {@link LocationVariable}.
         */
        private final BiFunction<NPC, LocationVariable, String> resolveFunction;

        ARGUMENT(final BiFunction<NPC, LocationVariable, String> resolve) {
            this.resolveFunction = resolve;
        }

        /**
         * Resolve this argument from the given NPC. The location variable is optional.
         *
         * @param npc      NPC to resolve from
         * @param location location variable to use for resolving
         * @return the value that the variable resolved to
         */
        public String resolve(final NPC npc, @Nullable final LocationVariable location) {
            return resolveFunction.apply(npc, location);
        }
    }
}
