package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Locale;

/**
 * Timer objective that tracks the ingame time when the conditions are fulfilled.
 */
public class TimerObjective extends CountingObjective implements Runnable {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * An optional DisplayName for the objective.
     */
    private final Argument<String> name;

    /**
     * Actions to run before the objective is actually removed.
     */
    private final Argument<List<ActionID>> doneActions;

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
     * @param service      the objective factory service.
     * @param targetAmount the target amount for the objective.
     * @param questTypeApi the QuestTypeApi instance.
     * @param name         the name of the objective.
     * @param interval     the interval to check the conditions and progress the objective.
     * @param doneActions  actions to run before the objective is actually removed.
     * @throws QuestException if an error occurs while creating the objective.
     */
    public TimerObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount, final QuestTypeApi questTypeApi, final Argument<String> name,
                          final Argument<Number> interval, final Argument<List<ActionID>> doneActions) throws QuestException {
        super(service, targetAmount, null);
        this.questTypeApi = questTypeApi;
        this.name = name;
        this.doneActions = doneActions;
        this.interval = interval.getValue(null).intValue();
        this.runnable = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), this, this.interval * 20L, this.interval * 20L);
    }

    @Override
    public void close() {
        runnable.cancel();
        super.close();
    }

    @Override
    public void run() {
        dataMap.keySet().forEach(profile -> {
            if (checkConditions(profile)) {
                getCountingData(profile).progress(interval);
                completeIfDoneOrNotify(profile);
            }
        });
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        final String lowerName = name.toLowerCase(Locale.ROOT);
        if ("name".equals(lowerName)) {
            return this.name.getValue(profile);
        }
        return super.getProperty(name, profile);
    }

    /**
     * Checks if the objective gets completed.
     *
     * @param event   The event to check.
     * @param profile The profile of the player that completed the objective.
     */
    public void onPlayerObjectiveChange(final PlayerObjectiveChangeEvent event, final Profile profile) {
        qeHandler.handle(() -> {
            if (event.getObjective().equals(this) && containsPlayer(profile)
                    && event.getPreviousState() == ObjectiveState.ACTIVE && event.getState() == ObjectiveState.COMPLETED) {
                questTypeApi.actions(profile, doneActions.getValue(profile));
            }
        });
    }
}
