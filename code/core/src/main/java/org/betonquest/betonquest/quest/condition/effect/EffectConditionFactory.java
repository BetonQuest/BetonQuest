package org.betonquest.betonquest.quest.condition.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory for {@link EffectCondition}s.
 */
public class EffectConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the effect factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public EffectConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PotionEffectType> type = instruction.parse(PotionEffectTypeParser.POTION_EFFECT_TYPE).get();
        final BetonQuestLogger log = loggerFactory.create(EffectCondition.class);
        return new OnlineConditionAdapter(new EffectCondition(type), log, instruction.getPackage());
    }
}
