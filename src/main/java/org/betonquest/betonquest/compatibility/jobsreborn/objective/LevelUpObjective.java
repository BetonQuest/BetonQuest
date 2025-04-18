package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Objective that tracks the level up of a player in a specific job.
 */
public class LevelUpObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Job to level up.
     */
    private final VariableJob job;

    /**
     * Constructor for the LevelUpObjective.
     *
     * @param instruction the instruction of the objective
     * @param log         the logger for this objective
     * @param job         the job to level up
     * @throws QuestException if the instruction is invalid
     */
    public LevelUpObjective(final Instruction instruction, final BetonQuestLogger log, final VariableJob job) throws QuestException {
        super(instruction);
        this.log = log;
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
        try {
            if (event.getJob().isSame(this.job.getValue(profile)) && containsPlayer(profile) && checkConditions(profile)) {
                completeObjective(profile);
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Exception while processing jobs LevelUp Objective: " + e.getMessage(), e);
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
