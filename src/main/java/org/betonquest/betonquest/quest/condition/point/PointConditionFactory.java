package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create point conditions from {@link Instruction}s.
 */
public class PointConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the point condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public PointConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        final VariableNumber count = instruction.getVarNum();
        final boolean equal = instruction.hasArgument("equal");
        return new PointCondition(betonQuest, category, count, equal);
    }
}
