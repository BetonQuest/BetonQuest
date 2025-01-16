package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveLevelUpEvent extends Objective implements Listener {
    private final String sJobName;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ObjectiveLevelUpEvent(final Instruction instructions) throws QuestException {
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
    public void onJobsLevelUpEvent(final JobsLevelUpEvent event) {
        if (event.getJobName().equalsIgnoreCase(this.sJobName)) {
            final Profile profile = PlayerConverter.getID(event.getPlayer().getPlayer());
            if (containsPlayer(profile) && checkConditions(profile)) {
                completeObjective(profile);
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
