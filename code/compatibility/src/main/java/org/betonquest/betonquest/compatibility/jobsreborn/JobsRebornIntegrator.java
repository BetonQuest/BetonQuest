package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.jobsreborn.action.AddExpActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.action.AddLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.action.DelLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.action.JoinJobActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.action.LeaveJobActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.action.SetLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.CanLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.HasJobConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobFullConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.JoinJobObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.LeaveJobObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.LevelUpObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.PaymentObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for JobsReborn.
 */
public class JobsRebornIntegrator extends IntegrationTemplate {

    /**
     * The default constructor.
     */
    public JobsRebornIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("canlevel", new CanLevelConditionFactory());
        playerCondition("hasjob", new HasJobConditionFactory());
        playerCondition("jobfull", new JobFullConditionFactory());
        playerCondition("joblevel", new JobLevelConditionFactory());

        playerAction("addexp", new AddExpActionFactory());
        playerAction("addlevel", new AddLevelActionFactory());
        playerAction("dellevel", new DelLevelActionFactory());
        playerAction("joinjob", new JoinJobActionFactory());
        playerAction("leavejob", new LeaveJobActionFactory());
        playerAction("setlevel", new SetLevelActionFactory());

        objective("joinjob", new JoinJobObjectiveFactory());
        objective("leavejob", new LeaveJobObjectiveFactory());
        objective("levelup", new LevelUpObjectiveFactory());
        objective("payment", new PaymentObjectiveFactory(api.loggerFactory(), BetonQuest.getInstance().getPluginMessage()));

        registerFeatures(api, "nujobs_");
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
