package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOProfessionParser;

/**
 * Factory to create {@link MMOCoreProfessionLevelCondition}s from {@link Instruction}s.
 */
public class MMOCoreProfessionLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new MMO Core Condition Factory.
     */
    public MMOCoreProfessionLevelConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Profession> profession = instruction.parse(MMOProfessionParser.PROFESSION).get();
        final Argument<Number> targetLevel = instruction.number().get();
        final FlagArgument<Boolean> mustBeEqual = instruction.bool().getFlag("equal", false);
        return new MMOCoreProfessionLevelCondition(profession, targetLevel, mustBeEqual);
    }
}
