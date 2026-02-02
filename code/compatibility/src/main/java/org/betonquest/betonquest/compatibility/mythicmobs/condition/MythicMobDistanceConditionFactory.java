package org.betonquest.betonquest.compatibility.mythicmobs.condition;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link MythicMobDistanceCondition}s from {@link Instruction}s.
 */
public class MythicMobDistanceConditionFactory implements PlayerConditionFactory {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The Mob Executor used to get MythicMobs.
     */
    private final MobExecutor mobExecutor;

    /**
     * The parser for the mythic mob type.
     */
    private final SimpleArgumentParser<MythicMob> mob;

    /**
     * Create a new factory for {@link MythicMobDistanceCondition}s.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param mobExecutor   the mob executor used to get MythicMobs
     * @param mob           the parser for the mythic mob type
     */
    public MythicMobDistanceConditionFactory(final BetonQuestLoggerFactory loggerFactory, final MobExecutor mobExecutor,
                                             final SimpleArgumentParser<MythicMob> mob) {
        this.loggerFactory = loggerFactory;
        this.mobExecutor = mobExecutor;
        this.mob = mob;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<MythicMob> mobType = instruction.parse(mob).get();
        final Argument<Number> distance = instruction.number().get();
        return new OnlineConditionAdapter(new MythicMobDistanceCondition(mobExecutor, mobType, distance),
                loggerFactory.create(MythicMobDistanceCondition.class),
                instruction.getPackage());
    }
}
