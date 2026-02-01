package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.menu.OpenedMenu;
import org.betonquest.betonquest.menu.RPGMenu;

/**
 * Factory to create {@link MenuAction}s from {@link Instruction}s.
 */
public class MenuActionFactory implements PlayerActionFactory {

    /**
     * Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * RPGMenu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Create a new factory for Menu Actions.
     *
     * @param loggerFactory the factory to create new class specific logger
     * @param rpgMenu       the rpg menu instance
     */
    public MenuActionFactory(final BetonQuestLoggerFactory loggerFactory, final RPGMenu rpgMenu) {
        this.loggerFactory = loggerFactory;
        this.rpgMenu = rpgMenu;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Operation operation = instruction.enumeration(Operation.class).get().getValue(null);
        final QuestConsumer<OnlineProfile> consumer = switch (operation) {
            case OPEN -> {
                final Argument<MenuIdentifier> menuID = instruction.identifier(MenuIdentifier.class).get();
                yield profile -> rpgMenu.openMenu(profile, menuID.getValue(profile));
            }
            case CLOSE -> RPGMenu::closeMenu;
            case UPDATE -> profile -> {
                final OpenedMenu menu = OpenedMenu.getMenu(profile);
                if (menu != null) {
                    menu.update();
                }
            };
        };
        return new OnlineActionAdapter(new MenuAction(consumer), loggerFactory.create(MenuAction.class), instruction.getPackage());
    }

    /**
     * The operation of the action.
     */
    public enum Operation {
        /**
         * Opens a menu.
         */
        OPEN,
        /**
         * Closes any open menu.
         */
        CLOSE,
        /**
         * Updates the currently opened menu.
         */
        UPDATE
    }
}
