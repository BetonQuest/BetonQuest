package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates new {@link PickRandomEvent} instances from an {@link Instruction}.
 */
public class PickRandomEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The character used to separate the percentage and event in the instruction.
     */
    private static final Pattern EVENT_WEIGHT = Pattern.compile("(?<weight>\\d+\\.?\\d?)~(?<event>.+)");

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the PickRandomEventFactory.
     *
     * @param questTypeApi the Quest Type API
     */
    public PickRandomEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createPickRandomEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createPickRandomEvent(instruction);
    }

    private NullableEventAdapter createPickRandomEvent(final Instruction instruction) throws QuestException {
        final Variable<List<RandomEvent>> events = instruction.getList(string -> {
            final Matcher matcher = EVENT_WEIGHT.matcher(string);
            if (!matcher.matches()) {
                throw new QuestException("Weight must be specified correctly: " + string);
            }

            final String weightString = matcher.group("weight");
            final String eventString = matcher.group("event");
            final EventID eventID = instruction.get(eventString, EventID::new).getValue(null);
            final double weight = instruction.getParsers().number().apply(weightString).doubleValue();
            return new RandomEvent(eventID, weight);
        });
        final Variable<Number> amount = instruction.getValue("amount", instruction.getParsers().number());
        return new NullableEventAdapter(new PickRandomEvent(events, amount, questTypeApi));
    }
}
