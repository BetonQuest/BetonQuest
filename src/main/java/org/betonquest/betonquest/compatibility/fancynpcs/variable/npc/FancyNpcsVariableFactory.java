package org.betonquest.betonquest.compatibility.fancynpcs.variable.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.variables.LocationVariable;

/**
 * Factory to create {@link FancyNpcsVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %citizen.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return citizen name<br>
 * * full_name - Full FancyNpcs name<br>
 * * location - Return FancyNpcs location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 *
 * @see LocationVariable
 */
public class FancyNpcsVariableFactory implements PlayerlessVariableFactory {
    /**
     * Logger Factory for creating new Instruction logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory to create FancyNpcs NPC Variables.
     *
     * @param loggerFactory the logger factory creating new custom logger
     */
    public FancyNpcsVariableFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
        }
        final Argument key = instruction.getEnum(Argument.class);
        final LocationVariable location = key == Argument.LOCATION ? parseLocation(instruction) : null;
        return new FancyNpcsVariable(npcId, key, location);
    }

    private LocationVariable parseLocation(final Instruction instruction) throws InstructionParseException {
        try {
            final Instruction locationInstruction = new VariableInstruction(
                    loggerFactory.create(Instruction.class),
                    instruction.getPackage(),
                    new NoID(instruction.getPackage()),
                    "%location." + String.join(".", instruction.getRemainingParts()) + "%"
            );
            locationInstruction.current();
            return new LocationVariable(locationInstruction);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Could not generate dynamic location variable", e);
        }
    }
}
