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
        plugin.registerConditions("nujobs_canlevel", Condition_CanLevel.class);
        plugin.registerConditions("nujobs_hasjob", Condition_HasJob.class);
        plugin.registerConditions("nujobs_jobfull", Condition_JobFull.class);
        plugin.registerConditions("nujobs_joblevel", Condition_JobLevel.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Conditions [nujobs_canlevel,nujobs_hasjob,nujobs_jobfull,nujobs_joblevel]");

        //register events
        plugin.registerEvents("nujobs_addexp", Event_AddExp.class);
        plugin.registerEvents("nujobs_addlevel", Event_AddLevel.class);
        plugin.registerEvents("nujobs_dellevel", Event_DelLevel.class);
        plugin.registerEvents("nujobs_joinjob", Event_JoinJob.class);
        plugin.registerEvents("nujobs_leavejob", Event_LeaveJob.class);
        plugin.registerEvents("nujobs_setlevel", Event_SetLevel.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Events [nujobs_addexp,nujobs_addlevel,nujobs_dellevel,nujobs_joinjob,nujobs_leavejob,nujobs_setlevel]");

        //register objectives
        plugin.registerObjectives("nujobs_joinjob", Objective_JoinJob.class);
        plugin.registerObjectives("nujobs_leavejob", Objective_LeaveJob.class);
        plugin.registerObjectives("nujobs_levelup", Objective_LevelUpEvent.class);
        plugin.registerObjectives("nujobs_payment", Objective_PaymentEvent.class);
        LogUtils.getLogger().log(Level.INFO, "Registered Objectives [nujobs_joinjob,nujobs_leavejob,nujobs_levelup,nujobs_payment]");

    }

    @Override
    public void reload() {
    }

    @Override
    public void close() {

    }

}
