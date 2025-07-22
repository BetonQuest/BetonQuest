package org.betonquest.betonquest.quest.variable.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A factory for creating Point variables.
 */
public class PointVariableFactory extends AbstractPointVariableFactory<PlayerDataStorage> implements PlayerVariableFactory {

    /**
     * Create a new Point variable factory.
     *
     * @param dataStorage the player data storage
     * @param logger      the logger instance for this factory
     */
    public PointVariableFactory(final PlayerDataStorage dataStorage, final BetonQuestLogger logger) {
        super(dataStorage, logger);
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Triple<String, Integer, PointCalculationType> values = parseInstruction(instruction);
        return new PointVariable(dataHolder, values.getLeft(), values.getMiddle(), values.getRight());
    }
}
