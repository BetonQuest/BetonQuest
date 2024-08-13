package org.betonquest.betonquest.compatibility.npcs.citizens.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplierSupplier;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.distance.NPCDistanceCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.distance.NPCDistanceConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Citizens implementation of {@link NPCDistanceCondition}.
 */
public class CitizensDistanceConditionFactory extends NPCDistanceConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter supplier
     * @param data             the data for primary server thread access
     * @param loggerFactory    the logger factory to create class specific logger
     */
    public CitizensDistanceConditionFactory(final NPCAdapterSupplierSupplier supplierStandard, final PrimaryServerThreadData data, final BetonQuestLoggerFactory loggerFactory) {
        super(supplierStandard, loggerFactory);
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerCondition(super.parsePlayer(instruction), data);
    }
}
