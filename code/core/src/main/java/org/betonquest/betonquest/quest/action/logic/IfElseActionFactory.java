package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;

/**
 * Factory to create if-else actions from {@link Instruction}s.
 */
public class IfElseActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The keyword to identify an else action following.
     */
    private static final String ELSE_KEYWORD = "else";

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The action constructor.
     *
     * @param questTypeApi the Quest Type API
     */
    public IfElseActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createIfElseAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createIfElseAction(instruction);
    }

    private NullableActionAdapter createIfElseAction(final Instruction instruction) throws QuestException {
        final Argument<ConditionIdentifier> condition = instruction.identifier(ConditionIdentifier.class).get();
        final Argument<ActionIdentifier> action = instruction.identifier(ActionIdentifier.class).get();
        if (!ELSE_KEYWORD.equalsIgnoreCase(instruction.nextElement())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final Argument<ActionIdentifier> elseAction = instruction.identifier(ActionIdentifier.class).get();
        return new NullableActionAdapter(new IfElseAction(condition, action, elseAction, questTypeApi));
    }
}
