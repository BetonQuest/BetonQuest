package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.api.JobsLeaveEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveLeaveJob extends Objective implements Listener {
    /**
     * Job to leave.
     */
    private final VariableJob job;

    public ObjectiveLeaveJob(final Instruction instructions) throws QuestException {
        super(instructions);
        this.job = instructions.get(VariableJob::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobsLeaveEvent(final JobsLeaveEvent event) throws QuestException {
        final OnlineProfile onlineProfile = BetonQuest.getInstance().getProfileProvider().getProfile(event.getPlayer().getPlayer());
        if (event.getJob().isSame(this.job.getValue(onlineProfile)) && containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
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
