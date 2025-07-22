package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;

/**
 * Factory for creating {@link MenuObjective} instances from {@link Instruction}s.
 */
public class MenuObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The RPGMenu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Creates a new instance of the MenuObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param rpgMenu       the rpg menu instance
     */
    public MenuObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final RPGMenu rpgMenu) {
        this.loggerFactory = loggerFactory;
        this.rpgMenu = rpgMenu;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<MenuID> menuID = instruction.get(MenuID::new);
        final BetonQuestLogger log = loggerFactory.create(MenuObjective.class);
        return new MenuObjective(instruction, log, rpgMenu, menuID);
    }
}
