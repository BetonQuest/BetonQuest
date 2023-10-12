package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Folder event is a collection of other events, that can be run after a delay and the events can be randomly chosen to
 * run or not.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FolderEvent extends QuestEvent implements Listener {
    private final Random randomGenerator = new Random();

    private final VariableNumber delay;

    private final VariableNumber period;

    private final VariableNumber random;

    private final EventID[] events;

    private final boolean ticks;

    private final boolean minutes;

    private final boolean cancelOnLogout;

    private final List<UUID> cancelled;

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
        cancelOnLogout = instruction.hasArgument("cancelOnLogout");
        cancelled = new ArrayList<>();
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
            register();
            new BukkitRunnable() {
                @Override
                public void run() {
                    unregister();
                    if (cancelled.remove(profile.getProfileUUID())) {
                        return;
                    }
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
                register();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final EventID event = chosenList.remove(0);
                        if (cancelled.remove(profile.getProfileUUID()) || event == null) {
                            unregister();
                            this.cancel();
                            return;
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
        } else if (!ticks) {
            time *= 20;
        }
        return time;
    }

    private void register() {
        BetonQuest.getInstance().getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    private void unregister() {
        PlayerQuitEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (cancelOnLogout) {
            cancelled.add(PlayerConverter.getID(event.getPlayer()).getProfileUUID());
        }
    }
}
