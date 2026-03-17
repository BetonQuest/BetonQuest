package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
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

/**
 * Integrator for JobsReborn.
 */
public class JobsRebornIntegrator implements Integration {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public JobsRebornIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ConditionRegistry conditionRegistry = api.conditions().registry();
        conditionRegistry.register("nujobs_canlevel", new CanLevelConditionFactory());
        conditionRegistry.register("nujobs_hasjob", new HasJobConditionFactory());
        conditionRegistry.register("nujobs_jobfull", new JobFullConditionFactory());
        conditionRegistry.register("nujobs_joblevel", new JobLevelConditionFactory());

        final ActionRegistry actionRegistry = api.actions().registry();
        actionRegistry.register("nujobs_addexp", new AddExpActionFactory());
        actionRegistry.register("nujobs_addlevel", new AddLevelActionFactory());
        actionRegistry.register("nujobs_dellevel", new DelLevelActionFactory());
        actionRegistry.register("nujobs_joinjob", new JoinJobActionFactory());
        actionRegistry.register("nujobs_leavejob", new LeaveJobActionFactory());
        actionRegistry.register("nujobs_setlevel", new SetLevelActionFactory());

        final ObjectiveRegistry objectiveRegistry = api.objectives().registry();
        objectiveRegistry.register("nujobs_joinjob", new JoinJobObjectiveFactory());
        objectiveRegistry.register("nujobs_leavejob", new LeaveJobObjectiveFactory());
        objectiveRegistry.register("nujobs_levelup", new LevelUpObjectiveFactory());
        objectiveRegistry.register("nujobs_payment", new PaymentObjectiveFactory(api.loggerFactory(), plugin.getPluginMessage()));
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
