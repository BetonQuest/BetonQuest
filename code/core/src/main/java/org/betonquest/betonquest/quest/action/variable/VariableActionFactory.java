package org.betonquest.betonquest.quest.action.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create variable actions from {@link Instruction}s.
 */
public class VariableActionFactory implements PlayerActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new factory for {@link VariableAction}s.
     *
     * @param questTypeApi the Quest Type API
     */
    public VariableActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ObjectiveIdentifier> objectiveID = instruction.identifier(ObjectiveIdentifier.class).get();
        final Argument<String> key = instruction.string().get();
        final Argument<String> value = instruction.string().get();
        return new VariableAction(questTypeApi, objectiveID, key, value);
    }
}
