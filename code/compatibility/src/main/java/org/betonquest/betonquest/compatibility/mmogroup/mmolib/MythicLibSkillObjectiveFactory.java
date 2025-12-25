package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;
import java.util.Locale;

/**
 * Factory for creating {@link MythicLibSkillObjective} instances from {@link Instruction}s.
 */
public class MythicLibSkillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MythicLibSkillObjectiveFactory.
     */
    public MythicLibSkillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> skillId = instruction.string().get();
        final List<TriggerType> triggerTypes = instruction
                .parse(id -> TriggerType.valueOf(id.toUpperCase(Locale.ROOT)))
                .getList().getValue(null);
        return new MythicLibSkillObjective(instruction, skillId, triggerTypes);
    }
}
