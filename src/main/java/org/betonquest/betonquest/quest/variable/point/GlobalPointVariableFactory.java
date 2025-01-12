package org.betonquest.betonquest.quest.variable.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * A factory for creating Global Point variables.
 */
public class GlobalPointVariableFactory extends AbstractPointVariableFactory implements PlayerlessVariableFactory {

    /**
     * Create a new Point variable factory.
     *
     * @param betonQuest the BetonQuest instance
     * @param logger     the logger instance for this factory
     */
    public GlobalPointVariableFactory(final BetonQuest betonQuest, final BetonQuestLogger logger) {
        super(betonQuest, logger);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        final Triple<String, Integer, PointCalculationType> values = parseInstruction(instruction);
        return new GlobalPointVariable(betonQuest, values.getLeft(), values.getMiddle(), values.getRight());
    }
}
