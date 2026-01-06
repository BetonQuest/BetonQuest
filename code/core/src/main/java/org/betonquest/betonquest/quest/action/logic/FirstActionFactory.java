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

import java.util.List;

/**
 * Factory to create FirstActions from actions from {@link Instruction}s.
 */
public class FirstActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Empty constructor.
     *
     * @param questTypeApi the Quest Type API
     */
    public FirstActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createFirstAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createFirstAction(instruction);
    }

    private NullableActionAdapter createFirstAction(final Instruction instruction) throws QuestException {
        final Argument<List<ActionID>> list = instruction.parse(ActionID::new).list().get();
        return new NullableActionAdapter(new FirstAction(list, questTypeApi));
    }
}
