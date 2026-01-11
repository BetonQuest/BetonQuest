package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;
import org.bukkit.event.EventPriority;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<MenuIdentifier> menuID = instruction.identifier(MenuIdentifier.class).get();
        final BetonQuestLogger log = loggerFactory.create(MenuObjective.class);
        final MenuObjective objective = new MenuObjective(service, log, rpgMenu, menuID);
        service.request(MenuOpenEvent.class).priority(EventPriority.MONITOR).handler(objective::onMenuOpen)
                .profile(MenuOpenEvent::getProfile).subscribe(true);
        return objective;
    }
}
