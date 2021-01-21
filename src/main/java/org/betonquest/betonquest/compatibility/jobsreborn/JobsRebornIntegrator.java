package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.utils.LogUtils;

import java.util.logging.Level;


@SuppressWarnings("PMD.CommentRequired")
public class JobsRebornIntegrator implements Integrator {

    private final BetonQuest plugin;

    public JobsRebornIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        //Register conditions with beton
        plugin.registerConditions("nujobs_canlevel", ConditionCanLevel.class);
        plugin.registerConditions("nujobs_hasjob", ConditionHasJob.class);
        plugin.registerConditions("nujobs_jobfull", ConditionJobFull.class);
        plugin.registerConditions("nujobs_joblevel", ConditionJobLevel.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Conditions [nujobs_canlevel,nujobs_hasjob,nujobs_jobfull,nujobs_joblevel]");

        //register events
        plugin.registerEvents("nujobs_addexp", EventAddExp.class);
        plugin.registerEvents("nujobs_addlevel", EventAddLevel.class);
        plugin.registerEvents("nujobs_dellevel", EventDelLevel.class);
        plugin.registerEvents("nujobs_joinjob", EventJoinJob.class);
        plugin.registerEvents("nujobs_leavejob", EventLeaveJob.class);
        plugin.registerEvents("nujobs_setlevel", EventSetLevel.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Events [nujobs_addexp,nujobs_addlevel,nujobs_dellevel,nujobs_joinjob,nujobs_leavejob,nujobs_setlevel]");

        //register objectives
        plugin.registerObjectives("nujobs_joinjob", ObjectiveJoinJob.class);
        plugin.registerObjectives("nujobs_leavejob", ObjectiveLeaveJob.class);
        plugin.registerObjectives("nujobs_levelup", ObjectiveLevelUpEvent.class);
        plugin.registerObjectives("nujobs_payment", ObjectivePaymentEvent.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Objectives [nujobs_joinjob,nujobs_leavejob,nujobs_levelup,nujobs_payment]");

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
