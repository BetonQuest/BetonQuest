package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link NpcRangeObjective} instances from {@link DefaultInstruction}s.
 */
public class NpcRangeObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the NpcRangeObjectiveFactory.
     */
    public NpcRangeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<List<NpcID>> npcIds = instruction.getList(NpcID::new);
        final Variable<Trigger> trigger = instruction.get(Argument.ENUM(Trigger.class));
        final Variable<Number> radius = instruction.get(Argument.NUMBER);
        return new NpcRangeObjective(instruction, npcIds, radius, trigger);
    }
}
