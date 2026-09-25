package org.betonquest.betonquest.quest.condition.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.PotionEffectTypeParser;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory for {@link EffectCondition}s.
 */
public class EffectConditionFactory implements PlayerConditionFactory {

    /**
     * Create the effect factory.
     */
    public EffectConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PotionEffectType> type = instruction.parse(PotionEffectTypeParser.POTION_EFFECT_TYPE).get();
        return new OnlineConditionAdapter(new EffectCondition(type));
    }
}
