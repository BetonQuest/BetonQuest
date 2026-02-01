package org.betonquest.betonquest.quest.action.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory for the delete global point action.
 */
public class DeleteGlobalPointActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates a new DeleteGlobalPointActionFactory.
     *
     * @param globalData the global data
     */
    public DeleteGlobalPointActionFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointAction(instruction);
    }

    private NullableActionAdapter createDeleteGlobalPointAction(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        return new NullableActionAdapter(new DeleteGlobalPointAction(globalData, category));
    }
}
