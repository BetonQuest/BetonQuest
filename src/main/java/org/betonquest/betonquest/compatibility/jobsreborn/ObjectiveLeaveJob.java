package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveLeaveJob extends Objective implements Listener {
    private final String sJobName;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ObjectiveLeaveJob(final Instruction instructions) throws QuestException {
        super(instructions);
        if (instructions.size() < 2) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instructions.getPart(1))) {
                sJobName = job.getName();
                return;
            }
        }
        throw new QuestException("Jobs Reborn job " + instructions.getPart(1) + " does not exist");
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobsLeaveEvent(final JobsLeaveEvent event) {
        if (event.getJob().getName().equalsIgnoreCase(this.sJobName)) {
            final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer().getPlayer());
            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
