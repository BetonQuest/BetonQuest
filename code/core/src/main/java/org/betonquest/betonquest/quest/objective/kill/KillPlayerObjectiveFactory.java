package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating {@link KillPlayerObjective} instances from {@link Instruction}s.
 */
public class KillPlayerObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the KillPlayerObjectiveFactory.
     */
    public KillPlayerObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<String> name = instruction.string().get("name").orElse(null);
        final Argument<List<ConditionID>> required = instruction.parse(ConditionID::new)
                .getList("required", Collections.emptyList());
        return new KillPlayerObjective(instruction, targetAmount, name, required);
    }
}
