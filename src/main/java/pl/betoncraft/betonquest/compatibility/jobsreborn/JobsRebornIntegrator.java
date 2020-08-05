/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.compatibility.jobsreborn;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;


public class JobsRebornIntegrator implements Integrator {

    private BetonQuest plugin;

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
    }

    @Override
    public void close() {

    }

}
