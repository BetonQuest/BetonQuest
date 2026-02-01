package org.betonquest.betonquest.quest.action.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates new {@link PickRandomAction} instances from an {@link Instruction}.
 */
public class PickRandomActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The character used to separate the percentage and action in the instruction.
     */
    private static final Pattern ACTION_WEIGHT = Pattern.compile("(?<weight>\\d+\\.?\\d?)~(?<action>.+)");

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the PickRandomActionFactory.
     *
     * @param questTypeApi the Quest Type API
     */
    public PickRandomActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createPickRandomAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createPickRandomAction(instruction);
    }

    private NullableActionAdapter createPickRandomAction(final Instruction instruction) throws QuestException {
        final Argument<List<RandomAction>> actions = instruction.parse(string -> {
            final Matcher matcher = ACTION_WEIGHT.matcher(string);
            if (!matcher.matches()) {
                throw new QuestException("Weight must be specified correctly: " + string);
            }

            final String weightString = matcher.group("weight");
            final String actionString = matcher.group("action");
            final ActionIdentifier actionID = instruction.chainForArgument(actionString).identifier(ActionIdentifier.class).get().getValue(null);
            final double weight = NumberParser.DEFAULT.apply(weightString).doubleValue();
            return new RandomAction(actionID, weight);
        }).list().get();
        final Argument<Number> amount = instruction.number().get("amount").orElse(null);
        return new NullableActionAdapter(new PickRandomAction(actions, amount, questTypeApi));
    }
}
