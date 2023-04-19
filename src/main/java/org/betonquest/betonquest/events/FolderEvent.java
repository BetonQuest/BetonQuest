package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Folder event is a collection of other events, that can be run after a delay and the events can be randomly chosen to
 * run or not.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FolderEvent extends QuestEvent {
    private final Random randomGenerator = new Random();

    private final VariableNumber delay;

    private final VariableNumber period;

    private final VariableNumber random;

    private final EventID[] events;

    private final boolean ticks;

    private final boolean minutes;

    public FolderEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        delay = instruction.getVarNum(instruction.getOptional("delay"));
        period = instruction.getVarNum(instruction.getOptional("period"));
        random = instruction.getVarNum(instruction.getOptional("random"));
        ticks = instruction.hasArgument("ticks");
        minutes = instruction.hasArgument("minutes");
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final ArrayList<EventID> chosenList = new ArrayList<>();
        // choose randomly which events should be fired
        final int randomInt = random == null ? 0 : random.getInt(profile);
        if (randomInt > 0 && randomInt <= events.length) {
            // copy events into the modifiable ArrayList
            final ArrayList<EventID> eventsList = new ArrayList<>(Arrays.asList(events));
            // remove chosen events from that ArrayList and place them in a new
            // list
            for (int i = randomInt; i > 0; i--) {
                final int chosen = randomGenerator.nextInt(eventsList.size());
                chosenList.add(eventsList.remove(chosen));
            }
        } else {
            chosenList.addAll(Arrays.asList(events));
        }

        final Long execDelay = getInTicks(delay, profile);
        final Long execPeriod = getInTicks(period, profile);

        if (execDelay == null && execPeriod == null) {
            for (final EventID event : chosenList) {
                BetonQuest.event(profile, event);
            }
        } else if (execPeriod == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final EventID event : chosenList) {
                        BetonQuest.event(profile, event);
                    }
                }
            }.runTaskLater(BetonQuest.getInstance(), execDelay);
        } else {
            if (execDelay == null && !chosenList.isEmpty()) {
                final EventID event = chosenList.remove(0);
                BetonQuest.event(profile, event);
            }
            if (!chosenList.isEmpty()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final EventID event = chosenList.remove(0);
                        if (chosenList.isEmpty()) {
                            this.cancel();
                        }
                        BetonQuest.event(profile, event);
                    }
                }.runTaskTimer(BetonQuest.getInstance(), execDelay == null ? execPeriod : execDelay, execPeriod);
            }
        }
        return null;
    }

    private Long getInTicks(final VariableNumber timeVariable, final Profile profile) throws QuestRuntimeException {
        if (timeVariable == null) {
            return null;
        }

        long time = timeVariable.getInt(profile);
        if (time == 0) {
            return null;
        }

        if (minutes) {
            time *= 20 * 60;
        }
        if (!ticks) {
            time *= 20;
        }
        return time;
    }

}
