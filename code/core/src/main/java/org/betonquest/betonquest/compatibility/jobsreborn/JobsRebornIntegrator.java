package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.CanLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.HasJobConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobFullConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.JobLevelConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.AddExpEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.AddLevelEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.DelLevelEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.JoinJobEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.LeaveJobEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.event.SetLevelEventFactory;
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
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("nujobs_canlevel", new CanLevelConditionFactory(data));
        conditionRegistry.register("nujobs_hasjob", new HasJobConditionFactory(data));
        conditionRegistry.register("nujobs_jobfull", new JobFullConditionFactory(data));
        conditionRegistry.register("nujobs_joblevel", new JobLevelConditionFactory(data));

        final EventRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("nujobs_addexp", new AddExpEventFactory(data));
        eventRegistry.register("nujobs_addlevel", new AddLevelEventFactory(data));
        eventRegistry.register("nujobs_dellevel", new DelLevelEventFactory(data));
        eventRegistry.register("nujobs_joinjob", new JoinJobEventFactory(data));
        eventRegistry.register("nujobs_leavejob", new LeaveJobEventFactory(data));
        eventRegistry.register("nujobs_setlevel", new SetLevelEventFactory(data));

        final FeatureTypeRegistry<Objective> objectiveRegistry = questRegistries.objective();
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
