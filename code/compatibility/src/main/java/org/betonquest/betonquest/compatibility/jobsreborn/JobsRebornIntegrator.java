package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.CanLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.HasJobConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobFullConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.AddExpActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.AddLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.DelLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.JoinJobActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.LeaveJobActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.SetLevelActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.JoinJobObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.LeaveJobObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.LevelUpObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.objective.PaymentObjectiveFactory;

/**
 * Integrator for JobsReborn.
 */
public class JobsRebornIntegrator implements Integrator {

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
    public void hook(final BetonQuestApi api) {
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("nujobs_canlevel", new CanLevelConditionFactory());
        conditionRegistry.register("nujobs_hasjob", new HasJobConditionFactory());
        conditionRegistry.register("nujobs_jobfull", new JobFullConditionFactory());
        conditionRegistry.register("nujobs_joblevel", new JobLevelConditionFactory());

        final ActionRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("nujobs_addexp", new AddExpActionFactory());
        eventRegistry.register("nujobs_addlevel", new AddLevelActionFactory());
        eventRegistry.register("nujobs_dellevel", new DelLevelActionFactory());
        eventRegistry.register("nujobs_joinjob", new JoinJobActionFactory());
        eventRegistry.register("nujobs_leavejob", new LeaveJobActionFactory());
        eventRegistry.register("nujobs_setlevel", new SetLevelActionFactory());

        final ObjectiveRegistry objectiveRegistry = questRegistries.objective();
        objectiveRegistry.register("nujobs_joinjob", new JoinJobObjectiveFactory());
        objectiveRegistry.register("nujobs_leavejob", new LeaveJobObjectiveFactory());
        objectiveRegistry.register("nujobs_levelup", new LevelUpObjectiveFactory());
        objectiveRegistry.register("nujobs_payment", new PaymentObjectiveFactory(api.getLoggerFactory(), plugin.getPluginMessage()));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
