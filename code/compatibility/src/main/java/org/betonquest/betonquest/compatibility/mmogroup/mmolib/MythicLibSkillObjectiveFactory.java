package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> skillId = instruction.string().get();
        final Optional<Argument<List<TriggerType>>> triggerTypes = instruction
                .parse(id -> TriggerType.valueOf(id.toUpperCase(Locale.ROOT)))
                .list().get("trigger");
        final MythicLibSkillObjective objective = new MythicLibSkillObjective(service, skillId, triggerTypes);
        service.request(SkillCastEvent.class).onlineHandler(objective::onSkillCast)
                .player(SkillCastEvent::getPlayer).subscribe(true);
        return objective;
    }
}
