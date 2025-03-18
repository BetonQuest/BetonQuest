package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.util.Utils;
import org.jetbrains.annotations.Nullable;

/**
 * A Variable that resolves into a {@link Job}.
 */
public class VariableJob extends Variable<Job> {
    /**
     * Resolves a string that may contain variables to a {@link Job}.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableJob(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input) throws QuestException {
        super(variableProcessor, pack, input, value
                -> Utils.getNN(Jobs.getJob(value), "Jobs Reborn job \"" + value + "\" does not exist"));
    }
}
