package org.betonquest.betonquest.quest.condition.advancement;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.Server;
import org.bukkit.advancement.Advancement;

/**
 * Factory to create advancement conditions from {@link Instruction}s.
 */
public class AdvancementConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The server instance to get advancements from.
     */
    private final Server server;

    /**
     * Create the Advancement Condition Factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param server        the server instance to get advancements from
     */
    public AdvancementConditionFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        this.loggerFactory = loggerFactory;
        this.server = server;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Advancement> advancement = instruction.namespacedKey().map(server::getAdvancement).get();
        return new OnlineConditionAdapter(new AdvancementCondition(advancement),
                loggerFactory.create(AdvancementCondition.class), instruction.getPackage());
    }
}
