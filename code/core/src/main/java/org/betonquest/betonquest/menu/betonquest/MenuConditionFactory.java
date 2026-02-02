package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link MenuCondition}s from {@link Instruction}s.
 */
public class MenuConditionFactory implements PlayerConditionFactory {

    /**
     * Factory to create a new class-specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for Menu Conditions.
     *
     * @param loggerFactory the factory to create a new class-specific logger
     */
    public MenuConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<MenuIdentifier> menuId = instruction.identifier(MenuIdentifier.class).get("id").orElse(null);
        return new OnlineConditionAdapter(new MenuCondition(menuId),
                loggerFactory.create(MenuCondition.class), instruction.getPackage());
    }
}
