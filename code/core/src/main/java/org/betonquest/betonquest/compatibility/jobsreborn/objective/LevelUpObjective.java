package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Objective that tracks the level up of a player in a specific job.
 */
public class LevelUpObjective extends Objective implements Listener {

    /**
     * Job to level up.
     */
    private final Variable<Job> job;

    /**
     * Constructor for the LevelUpObjective.
     *
     * @param instruction the instruction of the objective
     * @param job         the job to level up
     * @throws QuestException if the instruction is invalid
     */
    public LevelUpObjective(final Instruction instruction, final Variable<Job> job) throws QuestException {
        super(instruction);
        this.job = job;
    }

    /**
     * Handles the JobsLevelUpEvent.
     *
     * @param event the event that triggered the level up
     */
    @EventHandler(ignoreCancelled = true)
    public void onJobsLevelUpEvent(final JobsLevelUpEvent event) {
        final OnlineProfile profile = profileProvider.getProfile(event.getPlayer().getPlayer());
        qeHandler.handle(() -> {
            if (event.getJob().isSame(this.job.getValue(profile)) && containsPlayer(profile) && checkConditions(profile)) {
                completeObjective(profile);
            }
        });
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
