package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
        final Variable<String> skillId = instruction.get(Argument.STRING);
        final List<TriggerType> triggerTypes = parseTriggerTypes(instruction.getValue("trigger"));
        return new MythicLibSkillObjective(instruction, skillId, triggerTypes);
    }

    private List<TriggerType> parseTriggerTypes(@Nullable final String triggerTypeString) throws QuestException {
        final List<TriggerType> types = new ArrayList<>();
        if (triggerTypeString == null) {
            types.addAll(TriggerType.values());
            return types;
        }
        final Collection<String> possibleTypes = TriggerType.values().stream().map(TriggerType::name).toList();
        final String[] parts = triggerTypeString.toUpperCase(Locale.ROOT).split(",");
        for (final String part : parts) {
            if (!possibleTypes.contains(part)) {
                throw new QuestException("Unknown trigger type: " + part);
            }
            final TriggerType triggerType = TriggerType.valueOf(part);
            types.add(triggerType);
        }
        return types;
    }
}
