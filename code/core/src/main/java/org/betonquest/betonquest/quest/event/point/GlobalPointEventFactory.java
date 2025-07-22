package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.Locale;

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
        final String number = instruction.next();
        final String action = instruction.getValue("action");
        if (action != null) {
            try {
                final Point type = Point.valueOf(action.toUpperCase(Locale.ROOT));
                return new GlobalPointEvent(globalData, category, instruction.get(number, Argument.NUMBER), type);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new GlobalPointEvent(globalData, category, instruction.get(number.replace("*", ""), Argument.NUMBER), Point.MULTIPLY);
        }
        return new GlobalPointEvent(globalData, category, instruction.get(number, Argument.NUMBER), Point.ADD);
    }
}
