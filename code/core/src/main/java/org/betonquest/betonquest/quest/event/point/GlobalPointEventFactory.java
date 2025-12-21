package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create global points events from {@link Instruction}s.
 */
public class GlobalPointEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Create the global points event factory.
     *
     * @param globalData the global data
     */
    public GlobalPointEventFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    private NullableEventAdapter parseCombinedEvent(final Instruction instruction) throws QuestException {
        return new NullableEventAdapter(createGlobalPointEvent(instruction));
    }

    private GlobalPointEvent createGlobalPointEvent(final Instruction instruction) throws QuestException {
        final Variable<String> category = instruction.get(PackageArgument.IDENTIFIER);
        final Variable<Number> number = instruction.get(DefaultArgumentParsers.NUMBER);
        final PointType type = instruction.getValue("action", DefaultArgumentParsers.forEnumeration(PointType.class), PointType.ADD).getValue(null);
        return new GlobalPointEvent(globalData, category, number, type);
    }
}
