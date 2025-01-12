package org.betonquest.betonquest.quest.variable.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * A factory for creating Point variables.
 */
public class PointVariableFactory extends AbstractPointVariableFactory implements PlayerVariableFactory {

    /**
     * Create a new Point variable factory.
     *
     * @param betonQuest the BetonQuest instance
     * @param logger     the logger instance for this factory
     */
    public PointVariableFactory(final BetonQuest betonQuest, final BetonQuestLogger logger) {
        super(betonQuest, logger);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Triple<String, Integer, PointCalculationType> values = parseInstruction(instruction);
        return new PointVariable(betonQuest, values.getLeft(), values.getMiddle(), values.getRight());
    }
}
