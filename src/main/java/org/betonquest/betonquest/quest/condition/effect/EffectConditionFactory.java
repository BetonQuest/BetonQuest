package org.betonquest.betonquest.quest.condition.effect;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory for {@link EffectCondition}s.
 */
public class EffectConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the effect factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public EffectConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.next();
        final PotionEffectType type = Utils.getNN(PotionEffectType.getByName(string), "Effect " + string + " does not exist");
        final BetonQuestLogger log = loggerFactory.create(EffectCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new EffectCondition(type), log, instruction.getPackage()), data);
    }
}
