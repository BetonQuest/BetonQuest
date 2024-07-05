package org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.variables.LocationVariable;

import java.util.function.Supplier;

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
public class NPCVariableFactory extends NPCFactory implements PlayerlessVariableFactory {
    /**
     * Logger Factory for creating new Instruction logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory to create NPC Variables.
     *
     * @param loggerFactory    the logger factory creating new custom logger
     * @param supplierSupplier the supplier providing the npc adapter supplier
     */
    public NPCVariableFactory(final BetonQuestLoggerFactory loggerFactory, final Supplier<NPCSupplierStandard> supplierSupplier) {
        super(supplierSupplier);
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final Supplier<BQNPCAdapter<?>> npcSupplier = getSupplierByID(instruction.next());
        final Argument key = instruction.getEnum(Argument.class);
        final LocationVariable location = key == Argument.LOCATION ? parseLocation(instruction) : null;
        return new NPCVariable(npcSupplier, key, location);
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
