package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<PlayerAttribute> attribute = instruction.parse(MMOAttributeParser.ATTRIBUTE).get();
        final Argument<Number> targetLevel = instruction.number().get();
        final boolean mustBeEqual = instruction.hasArgument("equal");
        return new MMOCoreAttributeCondition(attribute, targetLevel, mustBeEqual);
    }
}
