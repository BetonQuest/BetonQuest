package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOAttributeParser;

/**
 * Factory to create {@link MMOCoreAttributeCondition}s from {@link Instruction}s.
 */
public class MMOCoreAttributeConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new MMO Core Condition Factory.
     */
    public MMOCoreAttributeConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<PlayerAttribute> attribute = instruction.get(MMOAttributeParser.ATTRIBUTE);
        final Variable<Number> targetLevelVar = instruction.get(instruction.getParsers().number());
        final boolean mustBeEqual = instruction.hasArgument("equal");
        return new MMOCoreAttributeCondition(attribute, targetLevelVar, mustBeEqual);
    }
}
