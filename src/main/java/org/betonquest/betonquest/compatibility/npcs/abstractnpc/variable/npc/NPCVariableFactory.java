package org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplierSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.variables.LocationVariable;

/**
 * Factory to create {@link NPCVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %<variableName>.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return npc name<br>
 * * full_name - Full npc name<br>
 * * location - Return npc location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 *
 * @see LocationVariable
 */
public class NPCVariableFactory implements PlayerlessVariableFactory {
    /**
     * Logger Factory for creating new Instruction logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NPCAdapterSupplierSupplier supplierStandard;

    /**
     * Create a new factory to create NPC Variables.
     *
     * @param supplierStandard the supplier providing the npc adapter
     * @param loggerFactory    the logger factory creating new custom logger
     */
    public NPCVariableFactory(final NPCAdapterSupplierSupplier supplierStandard, final BetonQuestLoggerFactory loggerFactory) {
        this.supplierStandard = supplierStandard;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final NPCAdapterSupplier npcSupplier = supplierStandard.getSupplierByID(instruction.next());
        final Argument key = instruction.getEnum(Argument.class);
        final LocationVariable location = key == Argument.LOCATION ? parseLocation(instruction) : null;
        return new NPCVariable(npcSupplier, key, location, loggerFactory.create(NPCVariable.class));
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
