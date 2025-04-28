package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;

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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableList<NpcID> npcIds = instruction.get(PackageArgument.ofList(NpcID::new));
        final Variable<Trigger> trigger = instruction.getVariable(Argument.ENUM(Trigger.class));
        final Variable<Number> radius = instruction.getVariable(Argument.NUMBER);
        return new NpcRangeObjective(instruction, npcIds, radius, trigger);
    }
}
