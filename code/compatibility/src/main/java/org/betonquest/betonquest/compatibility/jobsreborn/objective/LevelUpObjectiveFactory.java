package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory for creating {@link LevelUpObjective} instances from {@link Instruction}s.
 */
public class LevelUpObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ObjectiveLevelUpEventFactory.
     */
    public LevelUpObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        final LevelUpObjective objective = new LevelUpObjective(instruction, job);
        service.request(JobsLevelUpEvent.class).onlineHandler(objective::onJobsLevelUpEvent)
                .player(event -> event.getPlayer().getPlayer()).subscribe(true);
        return objective;
    }
}
