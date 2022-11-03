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

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Pick random event is a collection of other events, which can be randomly chosen to run or not based on probability.
 * Other than folder you can specify which events are more likely to be run by adding the percentage.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PickRandomEvent extends QuestEvent {
    private final static char PERCENTAGE = '%';

    private final RandomGenerator randomGenerator;
    private final List<RandomEvent> events;
    private final VariableNumber amount;


    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    public PickRandomEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.persistent = true;
        super.staticness = true;
        this.randomGenerator = RandomGenerator.getDefault();
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
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getPackagePath(), parts[0]);
                return new RandomEvent(eventID, chance);
            } else if (count == 3) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[3]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage().getPackagePath(), "%" + parts[1] + "%");
                return new RandomEvent(eventID, chance);
            }
            throw new InstructionParseException("Error while loading event: '" + instruction.getEvent().getFullID() + "'. Wrong number of % detected. Check your event.");
        });
        this.amount = instruction.getVarNum(instruction.getOptional("amount"));
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final List<ResolvedRandomEvent> events = new ArrayList<>();
        for (final RandomEvent randomEvent : this.events) {
            events.add(new ResolvedRandomEvent(randomEvent, profile));
        }
        double total = events.stream().mapToDouble(ResolvedRandomEvent::chance).sum();

        int pick = this.amount == null ? 1 : this.amount.getInt(profile);
        while (pick > 0 && !events.isEmpty()) {
            pick--;
            double random = randomGenerator.nextDouble() * total;
            for (final ResolvedRandomEvent event : events) {
                random -= event.chance;
                if (random < 0) {
                    BetonQuest.event(profile, event.identifier);
                    events.remove(event);
                    total -= event.chance;
                    break;
                }
            }
        }
        return null;
    }

    private record RandomEvent(EventID identifier, VariableNumber chance) {
    }

    private record ResolvedRandomEvent(EventID identifier, double chance) {
        public ResolvedRandomEvent(final RandomEvent identifier, final Profile profile) throws QuestRuntimeException {
            this(identifier.identifier, identifier.chance.getDouble(profile));
        }
    }
}
