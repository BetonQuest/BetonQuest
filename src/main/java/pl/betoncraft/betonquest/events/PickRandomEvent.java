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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.EventID;

import java.util.ArrayList;
import java.util.List;

/**
 * Pick random event is a collection of other events, which can be randomly chosen to run or not based on probability.
 * Other than folder you can specify which events are more likely to be run by adding the percentage.
 *
 * @author Jonas Blocher
 */
public class PickRandomEvent extends QuestEvent {

    private final List<RandomEvent> events;
    private final VariableNumber amount;

    public PickRandomEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.persistent = true;
        super.staticness = true;
        this.events = instruction.getList(string -> {
            if (!string.matches("\\d+(\\.\\d+)?%[^%]*")) {
                throw new InstructionParseException("Percentage must be specified correctly: " + string);
            }
            final String[] parts = string.split("%");
            final EventID eventID;
            try {
                eventID = new EventID(instruction.getPackage(), parts[1]);
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
            }
            final VariableNumber chance = new VariableNumber(instruction.getPackage().getName(), parts[0]);
            return new RandomEvent(eventID, chance);
        });
        this.amount = instruction.getVarNum(instruction.getOptional("amount"));
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final List<RandomEvent> events = new ArrayList<>(this.events);
        double total = 0;
        // Calculate total amount of all "percentages" (so that it must not be 100)
        for (final RandomEvent event : events) {
            total += event.getChance().getDouble(playerID);
        }
        //pick as many events as given with pick optional (or 1 if amount wasn't specified)
        int pick = this.amount == null ? 1 : this.amount.getInt(playerID);
        while (pick-- > 0 && !events.isEmpty()) {
            //choose a random number between 0 and the total amount of percentages
            final double found = Math.random() * total;
            double current = 0;
            //go through all random events and pick the first one where the current sum is higher than the found random number
            inner:
            for (int i = 0; i < events.size(); i++) {
                final RandomEvent event = events.get(i);
                current += event.getChance().getDouble(playerID);
                if (current >= found) {
                    //run the event
                    BetonQuest.event(playerID, event.getIdentifier());
                    //remove the event from the list so that it's not picked again
                    events.remove(i);
                    total -= event.getChance().getDouble(playerID);
                    break inner;
                }
            }
        }
        return null;
    }

    private class RandomEvent {

        private final EventID identifier;
        private final VariableNumber chance;

        public RandomEvent(final EventID identifier, final VariableNumber chance) {
            this.identifier = identifier;
            this.chance = chance;
        }

        public EventID getIdentifier() {
            return identifier;
        }

        public VariableNumber getChance() {
            return chance;
        }
    }
}
