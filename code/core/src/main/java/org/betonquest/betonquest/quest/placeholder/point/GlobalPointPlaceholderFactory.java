package org.betonquest.betonquest.quest.placeholder.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * A factory for creating Global Point placeholders.
 */
public class GlobalPointPlaceholderFactory extends AbstractPointPlaceholderFactory<GlobalData> implements PlayerlessPlaceholderFactory {

    /**
     * Create a new Point placeholder factory.
     *
     * @param globalData the global data holder
     * @param logger     the logger instance for this factory
     */
    public GlobalPointPlaceholderFactory(final GlobalData globalData, final BetonQuestLogger logger) {
        super(globalData, logger);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        final Triple<String, Integer, PointCalculationType> values = parseInstruction(instruction);
        return new GlobalPointPlaceholder(dataHolder, values.getLeft(), values.getMiddle(), values.getRight());
    }
}
