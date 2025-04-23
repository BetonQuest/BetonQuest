package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates new {@link PickRandomEvent} instances from an {@link Instruction}.
 */
public class PickRandomEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * The character used to separate the percentage and event in the instruction.
     */
    private static final Pattern PERCENTAGE_EVENT = Pattern.compile("(?<percentage>\\d+\\.?\\d?)~(?<event>.+)");

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Creates the PickRandomEventFactory.
     *
     * @param questTypeAPI the Quest Type API
     */
    public PickRandomEventFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createPickRandomEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createPickRandomEvent(instruction);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private NullableEventAdapter createPickRandomEvent(final Instruction instruction) throws QuestException {
        final VariableList<RandomEvent> events = instruction.get(Argument.ofList(string -> {
            final Matcher matcher = PERCENTAGE_EVENT.matcher(string);
            if (!matcher.matches()) {
                throw new QuestException("Percentage must be specified correctly: " + string);
            }

            final String percentageString = matcher.group("percentage");
            final String eventString = matcher.group("event");
            final Double percentage = Double.parseDouble(percentageString);
            final EventID eventID = new EventID(instruction.getPackage(), eventString);
            return new RandomEvent(eventID, percentage);
        }));
        final VariableNumber amount = instruction.get(instruction.getOptional("amount"), VariableNumber::new);
        return new NullableEventAdapter(new PickRandomEvent(events, amount, questTypeAPI));
    }
}
