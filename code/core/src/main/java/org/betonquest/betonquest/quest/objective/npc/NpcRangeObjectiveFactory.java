package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;

import java.util.List;

/**
 * Factory for creating {@link NpcRangeObjective} instances from {@link Instruction}s.
 */
public class NpcRangeObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the NpcRangeObjectiveFactory.
     */
    public NpcRangeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<List<NpcID>> npcIds = instruction.parse(NpcID::new).list().get();
        final Argument<Trigger> trigger = instruction.enumeration(Trigger.class).get();
        final Argument<Number> radius = instruction.number().get();
        return new NpcRangeObjective(service, npcIds, radius, trigger);
    }
}
