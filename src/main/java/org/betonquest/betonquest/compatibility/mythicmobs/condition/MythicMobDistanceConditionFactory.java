package org.betonquest.betonquest.compatibility.mythicmobs.condition;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MythicMobDistanceCondition}s from {@link Instruction}s.
 */
public class MythicMobDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * API Helper for getting MythicMobs.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Data required for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for {@link MythicMobDistanceCondition}s.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param apiHelper     the api helper used get MythicMobs
     * @param data          the primary server thread data required for main thread checking
     */
    public MythicMobDistanceConditionFactory(final BetonQuestLoggerFactory loggerFactory, final BukkitAPIHelper apiHelper, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.apiHelper = apiHelper;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String internalName = instruction.next();
        if (apiHelper.getMythicMob(internalName) == null) {
            throw new QuestException("MythicMob with internal name '" + internalName + "' does not exist");
        }

        final Variable<Number> distance = instruction.get(Argument.NUMBER);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new MythicMobDistanceCondition(apiHelper, internalName, distance),
                loggerFactory.create(MythicMobDistanceCondition.class),
                instruction.getPackage()
        ), data);
    }
}
