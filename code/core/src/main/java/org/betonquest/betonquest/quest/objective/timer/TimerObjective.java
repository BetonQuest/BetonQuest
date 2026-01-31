package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Timer objective that tracks the ingame time when the conditions are fulfilled.
 */
public class TimerObjective extends CountingObjective implements Runnable {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Actions to run before the objective is actually removed.
     */
    private final Argument<List<ActionIdentifier>> doneActions;

    /**
     * The resolved interval in seconds.
     */
    private final int interval;

    /**
     * The scheduled task that runs the objective.
     */
    private final BukkitTask runnable;

    /**
     * Constructs a new TrackingObjective.
     *
     * @param service      the objective service.
     * @param targetAmount the target amount for the objective.
     * @param questTypeApi the QuestTypeApi instance.
     * @param name         the name of the objective.
     * @param interval     the interval to check the conditions and progress the objective.
     * @param doneActions  actions to run before the objective is actually removed.
     * @throws QuestException if an error occurs while creating the objective.
     */
    public TimerObjective(final ObjectiveService service, final Argument<Number> targetAmount, final QuestTypeApi questTypeApi, final Argument<String> name,
                          final Argument<Number> interval, final Argument<List<ActionIdentifier>> doneActions) throws QuestException {
        super(service, targetAmount, null);
        this.questTypeApi = questTypeApi;
        this.doneActions = doneActions;
        this.interval = interval.getValue(null).intValue();
        this.runnable = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), this, this.interval * 20L, this.interval * 20L);
        service.getProperties().setProperty("name", name::getValue);
    }

    @Override
    public void close() {
        runnable.cancel();
        super.close();
    }

    @Override
    public void run() {
        getService().getData().keySet().forEach(profile -> {
            getExceptionHandler().handle(() -> {
                if (getService().checkConditions(profile)) {
                    getCountingData(profile).progress(interval);
                    completeIfDoneOrNotify(profile);
                }
            });
        });
    }

    /**
     * Checks if the objective gets completed.
     *
     * @param event   The event to check.
     * @param profile The profile of the player that completed the objective.
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerObjectiveChange(final PlayerObjectiveChangeEvent event, final Profile profile) throws QuestException {
        if (event.getObjectiveID().equals(getObjectiveID()) && event.getPreviousState() == ObjectiveState.ACTIVE
                && event.getState() == ObjectiveState.COMPLETED) {
            questTypeApi.actions(profile, doneActions.getValue(profile));
        }
    }
}
