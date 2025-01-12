package org.betonquest.betonquest.compatibility.citizens.variable.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.variables.LocationVariable;

/**
 * Factory to create {@link CitizensVariable}s from {@link Instruction}s.
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
public class CitizensVariableFactory implements PlayerlessVariableFactory {
    /**
     * Logger Factory for creating new Instruction logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory to create Citizens NPC Variables.
     *
     * @param loggerFactory the logger factory creating new custom logger
     */
    public CitizensVariableFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        final Argument key = instruction.getEnum(Argument.class);
        final LocationVariable location = key == Argument.LOCATION ? parseLocation(instruction) : null;
        return new CitizensVariable(npcId, key, location);
    }

    private LocationVariable parseLocation(final Instruction instruction) throws QuestException {
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
            throw new QuestException("Could not generate dynamic location variable", e);
        }
    }
}
