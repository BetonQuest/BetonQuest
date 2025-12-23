package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.menu.MenuID;

/**
 * Factory to create {@link MenuCondition}s from {@link Instruction}s.
 */
public class MenuConditionFactory implements PlayerConditionFactory {

    /**
     * Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for Menu Conditions.
     *
     * @param loggerFactory the factory to create new class specific logger
     */
    public MenuConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<MenuID> menuId = instruction.getValue("id", MenuID::new);
        return new OnlineConditionAdapter(new MenuCondition(menuId),
                loggerFactory.create(MenuCondition.class), instruction.getPackage());
    }
}
