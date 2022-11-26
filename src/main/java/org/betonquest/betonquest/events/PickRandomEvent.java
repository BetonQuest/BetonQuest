package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;

import java.util.Iterator;
import java.util.LinkedList;
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

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    public PickRandomEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.persistent = true;
        super.staticness = true;
        this.events = instruction.getList(string -> {
            if (!string.matches("(\\d+\\.?\\d?|%.*%)%.+")) {
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
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getQuestPath(), parts[0]);
                return new RandomEvent(eventID, chance);
            } else if (count == 3) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[3]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getQuestPath(), "%" + parts[1] + "%");
                return new RandomEvent(eventID, chance);
            }
            throw new InstructionParseException("Error while loading event: '" + instruction.getEvent().getFullID() + "'. Wrong number of % detected. Check your event.");
        });
        this.amount = instruction.getVarNum(instruction.getOptional("amount"));
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final List<ResolvedRandomEvent> resolvedEvents = new LinkedList<>();
        for (final RandomEvent randomEvent : events) {
            resolvedEvents.add(randomEvent.resolveFor(profile));
        }
        double total = resolvedEvents.stream().mapToDouble(ResolvedRandomEvent::chance).sum();

        int pick = this.amount == null ? 1 : this.amount.getInt(profile);
        while (pick > 0 && !resolvedEvents.isEmpty()) {
            pick--;
            double random = Math.random() * total;
            final Iterator<ResolvedRandomEvent> iterator = resolvedEvents.iterator();
            while (iterator.hasNext()) {
                final ResolvedRandomEvent event = iterator.next();
                random -= event.chance;
                if (random < 0) {
                    BetonQuest.event(profile, event.eventID);
                    iterator.remove();
                    total -= event.chance;
                    break;
                }
            }
        }
        return null;
    }

    private record RandomEvent(EventID eventID, VariableNumber chance) {
        public ResolvedRandomEvent resolveFor(final Profile profile) throws QuestRuntimeException {
            return new ResolvedRandomEvent(eventID, chance.getDouble(profile));
        }
    }

    private record ResolvedRandomEvent(EventID eventID, double chance) {
    }
}
