package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MenuCondition}s from {@link Instruction}s.
 */
public class MenuConditionFactory implements PlayerConditionFactory {
    /**
     * Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Menu Conditions.
     *
     * @param loggerFactory the factory to create new class specific logger
     * @param data          the data used for primary server access
     */
    public MenuConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<MenuID> menuId = instruction.get(instruction.getOptional("id"), MenuID::new);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(new MenuCondition(menuId),
                loggerFactory.create(MenuCondition.class), instruction.getPackage()), data);
    }
}
