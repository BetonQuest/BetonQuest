package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parses a string to a job.
 */
public class JobParser implements SimpleArgumentParser<Job> {

    /**
     * The default instance of {@link JobParser}.
     */
    public static final JobParser JOB = new JobParser();

    /**
     * Creates a new parser for jobs.
     */
    public JobParser() {
    }

    @Override
    public Job apply(final String string) throws QuestException {
        final Job job = Jobs.getJob(string);
        if (job == null) {
            throw new QuestException("Jobs Reborn job '%s' does not exist!".formatted(string));
        }
        return job;
    }
}
