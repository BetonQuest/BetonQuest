package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.Utils;

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
        return Utils.getNN(Jobs.getJob(string), "Jobs Reborn job '" + string + "' does not exist");
    }
}
