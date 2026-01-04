package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
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
     * The character used to separate the percentage and action in the instruction.
     */
    private static final Pattern EVENT_WEIGHT = Pattern.compile("(?<weight>\\d+\\.?\\d?)~(?<action>.+)");

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the PickRandomActionFactory.
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
        final Argument<List<RandomAction>> actions = instruction.parse(string -> {
            final Matcher matcher = EVENT_WEIGHT.matcher(string);
            if (!matcher.matches()) {
                throw new QuestException("Weight must be specified correctly: " + string);
            }

            final String weightString = matcher.group("weight");
            final String actionString = matcher.group("action");
            final ActionID actionID = instruction.chainForArgument(actionString).parse(ActionID::new).get().getValue(null);
            final double weight = NumberParser.DEFAULT.apply(weightString).doubleValue();
            return new RandomAction(actionID, weight);
        }).list().get();
        final Argument<Number> amount = instruction.number().get("amount").orElse(null);
        return new NullableEventAdapter(new PickRandomEvent(actions, amount, questTypeApi));
    }
}
