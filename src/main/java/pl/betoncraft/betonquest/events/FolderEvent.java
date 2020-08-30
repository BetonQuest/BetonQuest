/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.EventID;

import java.util.ArrayList;
import java.util.Random;

/**
 * Folder event is a collection of other events, that can be run after a delay
 * and the events can be randomly chosen to run or not
 *
 * @author Jakub Sapalski
 */
public class FolderEvent extends QuestEvent {

    public VariableNumber delay;
    public VariableNumber random;
    public EventID[] events;
    public boolean ticks;
    public boolean minutes;

    public FolderEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        events = instruction.getList(e -> instruction.getEvent(e)).toArray(new EventID[0]);
        delay = instruction.getVarNum(instruction.getOptional("delay"));
        random = instruction.getVarNum(instruction.getOptional("random"));
        ticks = instruction.hasArgument("ticks");
        minutes = instruction.hasArgument("minutes");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final ArrayList<EventID> chosenList = new ArrayList<>();
        // choose randomly which events should be fired
        final int randomInt = random == null ? 0 : random.getInt(playerID);
        if (randomInt > 0 && randomInt <= events.length) {
            // copy events into the modifiable ArrayList
            final ArrayList<EventID> eventsList = new ArrayList<>();
            for (final EventID event : events) {
                eventsList.add(event);
            }
            // remove chosen events from that ArrayList and place them in a new
            // list
            for (int i = randomInt; i > 0; i--) {
                final int chosen = new Random().nextInt(eventsList.size());
                chosenList.add(eventsList.remove(chosen));
            }
        } else {
            // add all events if it's not random
            for (final EventID event : events) {
                chosenList.add(event);
            }
        }
        double time = (delay == null) ? 0d : delay.getDouble(playerID);
        if (ticks) {
            // do nothing
        } else if (minutes) {
            time *= 20 * 60;
        } else {
            time *= 20;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final EventID event : chosenList) {
                    BetonQuest.event(playerID, event);
                }
            }
        }.runTaskLater(BetonQuest.getInstance(), (int) time);
        return null;
    }

}
