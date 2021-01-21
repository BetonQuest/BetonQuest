package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;

import java.util.ArrayList;
import java.util.List;

/**
 * Pick random event is a collection of other events, which can be randomly chosen to run or not based on probability.
 * Other than folder you can specify which events are more likely to be run by adding the percentage.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PickRandomEvent extends QuestEvent {
    private final static char PERCENTAGE = '%';

    private final List<RandomEvent> events;
    private final VariableNumber amount;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public PickRandomEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.persistent = true;
        super.staticness = true;
        this.events = instruction.getList(string -> {
            if (!string.matches("(\\d+\\.?\\d?|%.*%)%\\w+")) {
                throw new InstructionParseException("Percentage must be specified correctly: " + string);
            }

            int index = 0;
            int count = 0;
            while (index < string.length()) {
                if (string.charAt(index) == PERCENTAGE) {
                    count++;
                }
                index++;
            }

            final String[] parts = string.split(String.valueOf(PERCENTAGE));
            final EventID eventID;

            if (count == 1) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[1]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getName(), parts[0]);
                return new RandomEvent(eventID, chance);
            } else if (count == 3) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[3]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getName(), "%" + parts[1] + "%");
                return new RandomEvent(eventID, chance);
            }
            throw new InstructionParseException("Error while loading event: '" + instruction.getEvent().getFullID() + "'. Wrong number of % detected. Check your event.");
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
        while (pick > 0 && !events.isEmpty()) {
            pick--;
            //choose a random number between 0 and the total amount of percentages
            final double found = Math.random() * total;
            double current = 0;
            //go through all random events and pick the first one where the current sum is higher than the found random number
            for (int i = 0; i < events.size(); i++) {
                final RandomEvent event = events.get(i);
                final double chance = event.getChance().getDouble(playerID);
                current += chance;
                if (current >= found) {
                    //run the event
                    BetonQuest.event(playerID, event.getIdentifier());
                    //remove the event from the list so that it's not picked again
                    events.remove(i);
                    total -= chance;
                    break;
                }
            }
        }
        return null;
    }

    private static class RandomEvent {

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
