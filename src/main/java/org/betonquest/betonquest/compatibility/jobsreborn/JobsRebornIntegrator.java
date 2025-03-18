package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionCanLevel;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionHasJob;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionJobFull;
import org.betonquest.betonquest.compatibility.jobsreborn.condition.FactoryConditionJobLevel;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventAddExp;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventAddLevel;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventDelLevel;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventJoinJob;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventLeaveJob;
import org.betonquest.betonquest.compatibility.jobsreborn.event.FactoryEventSetLevel;
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
        eventTypes.register("nujobs_addexp", new FactoryEventAddExp(data));
        eventTypes.register("nujobs_addlevel", new FactoryEventAddLevel(data));
        eventTypes.register("nujobs_dellevel", new FactoryEventDelLevel(data));
        eventTypes.register("nujobs_joinjob", new FactoryEventJoinJob(data));
        eventTypes.register("nujobs_leavejob", new FactoryEventLeaveJob(data));
        eventTypes.register("nujobs_setlevel", new FactoryEventSetLevel(data));
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
