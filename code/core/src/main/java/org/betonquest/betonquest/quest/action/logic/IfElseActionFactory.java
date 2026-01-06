package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.condition.ConditionID;

/**
 * Factory to create if-else events from {@link Instruction}s.
 */
public class IfElseActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The keyword to identify an else event following.
     */
    private static final String ELSE_KEYWORD = "else";

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The event constructor.
     *
     * @param questTypeApi the Quest Type API
     */
    public IfElseActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    private NullableActionAdapter createIfElseEvent(final Instruction instruction) throws QuestException {
        final Argument<ConditionID> condition = instruction.parse(ConditionID::new).get();
        final Argument<ActionID> event = instruction.parse(ActionID::new).get();
        if (!ELSE_KEYWORD.equalsIgnoreCase(instruction.nextElement())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final Argument<ActionID> elseEvent = instruction.parse(ActionID::new).get();
        return new NullableActionAdapter(new IfElseAction(condition, event, elseEvent, questTypeApi));
    }
}
