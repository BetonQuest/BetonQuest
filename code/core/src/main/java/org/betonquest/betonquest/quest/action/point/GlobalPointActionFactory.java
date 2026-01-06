package org.betonquest.betonquest.quest.action.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create global points events from {@link Instruction}s.
 */
public class GlobalPointActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Create the global points event factory.
     *
     * @param globalData the global data
     */
    public GlobalPointActionFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    private NullableActionAdapter parseCombinedEvent(final Instruction instruction) throws QuestException {
        return new NullableActionAdapter(createGlobalPointEvent(instruction));
    }

    private GlobalPointAction createGlobalPointEvent(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        final Argument<Number> number = instruction.number().get();
        final PointType type = instruction.enumeration(PointType.class).get("action", PointType.ADD).getValue(null);
        return new GlobalPointAction(globalData, category, number, type);
    }
}
