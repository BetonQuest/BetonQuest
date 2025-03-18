package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionCanLevel;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionHasJob;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionJobFull;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionJobLevel;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrator for JobsReborn.
 */
public class JobsRebornIntegrator implements Integrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public JobsRebornIntegrator() {
        plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(getClass());
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final QuestTypeRegistries questRegistries = BetonQuest.getInstance().getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("nujobs_canlevel", new FactoryConditionCanLevel(data));
        conditionTypes.register("nujobs_hasjob", new FactoryConditionHasJob(data));
        conditionTypes.register("nujobs_jobfull", new FactoryConditionJobFull(data));
        conditionTypes.register("nujobs_joblevel", new FactoryConditionJobLevel(data));
        log.info("Registered Conditions [nujobs_canlevel,nujobs_hasjob,nujobs_jobfull,nujobs_joblevel]");

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("nujobs_addexp", EventAddExp.class);
        eventTypes.register("nujobs_addlevel", EventAddLevel.class);
        eventTypes.register("nujobs_dellevel", EventDelLevel.class);
        eventTypes.register("nujobs_joinjob", EventJoinJob.class);
        eventTypes.register("nujobs_leavejob", EventLeaveJob.class);
        eventTypes.register("nujobs_setlevel", EventSetLevel.class);
        log.info("Registered Events [nujobs_addexp,nujobs_addlevel,nujobs_dellevel,nujobs_joinjob,nujobs_leavejob,nujobs_setlevel]");

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("nujobs_joinjob", ObjectiveJoinJob.class);
        objectiveTypes.register("nujobs_leavejob", ObjectiveLeaveJob.class);
        objectiveTypes.register("nujobs_levelup", ObjectiveLevelUpEvent.class);
        objectiveTypes.register("nujobs_payment", ObjectivePaymentEvent.class);
        log.info("Registered Objectives [nujobs_joinjob,nujobs_leavejob,nujobs_levelup,nujobs_payment]");
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
