package org.betonquest.betonquest.quest.objective.timer;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Locale;

/**
 * Timer objective that tracks the ingame time when the conditions are fulfilled.
 */
public class TimerObjective extends CountingObjective implements Listener, Runnable {
    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * An optional DisplayName for the objective.
     */
    private final Variable<String> name;

    /**
     * Events to run before the objective is actually removed.
     */
    private final Variable<List<EventID>> doneEvents;

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
     * @param instruction  the instruction that created this objective.
     * @param targetAmount the target amount for the objective.
     * @param questTypeApi the QuestTypeApi instance.
     * @param name         the name of the objective.
     * @param interval     the interval to check the conditions and progress the objective.
     * @param doneEvents   events to run before the objective is actually removed.
     * @throws QuestException if an error occurs while creating the objective.
     */
    public TimerObjective(final Instruction instruction, final Variable<Number> targetAmount, final QuestTypeApi questTypeApi, final Variable<String> name,
                          final Variable<Number> interval, final Variable<List<EventID>> doneEvents) throws QuestException {
        super(instruction, targetAmount, null);
        this.questTypeApi = questTypeApi;
        this.name = name;
        this.doneEvents = doneEvents;
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
        } else {
            return super.getProperty(name, profile);
        }
    }

    /**
     * Checks if the objective gets completed and runs the done events.
     *
     * @param event The event to check.
     */
    @EventHandler
    public void onPlayerObjectiveChange(final PlayerObjectiveChangeEvent event) {
        qeHandler.handle(() -> {
            if (event.getObjective().equals(this) && containsPlayer(event.getProfile())
                    && event.getPreviousState() == ObjectiveState.ACTIVE && event.getState() == ObjectiveState.COMPLETED) {
                for (final EventID doneEvent : doneEvents.getValue(event.getProfile())) {
                    questTypeApi.event(event.getProfile(), doneEvent);
                }
            }
        });
    }
}
