package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.distance;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.condition.OnlineProfileRequiredCondition;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCDistanceCondition}s from {@link Instruction}s.
 */
public class NPCDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NPCAdapterSupplier supplierStandard;

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter
     * @param loggerFactory    logger factory to use
     */
    public NPCDistanceConditionFactory(final NPCAdapterSupplier supplierStandard, final BetonQuestLoggerFactory loggerFactory) {
        this.supplierStandard = supplierStandard;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        final Supplier<BQNPCAdapter<?>> supplier = supplierStandard.getSupplierByID(npcId);
        final VariableNumber distance = instruction.getVarNum();
        return new OnlineProfileRequiredCondition(
                loggerFactory.create(NPCDistanceCondition.class),
                new NPCDistanceCondition(npcId, supplier, distance),
                instruction.getPackage());
    }
}
