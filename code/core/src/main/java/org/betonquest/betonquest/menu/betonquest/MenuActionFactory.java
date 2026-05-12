package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
     * RPGMenu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Create a new factory for Menu Actions.
     *
     * @param rpgMenu the rpg menu instance
     */
    public MenuActionFactory(final RPGMenu rpgMenu) {
        this.rpgMenu = rpgMenu;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Operation operation = instruction.enumeration(Operation.class).get().getValue(null);
        final QuestConsumer<OnlineProfile> consumer = switch (operation) {
            case OPEN -> {
                final Argument<MenuIdentifier> menuID = instruction.identifier(MenuIdentifier.class).get();
                final FlagArgument<Boolean> check = instruction.bool().getFlag("check", true);
                yield profile -> {
                    final MenuIdentifier resolvedId = menuID.getValue(profile);
                    if (check.getValue(profile).orElse(false)
                            && !rpgMenu.getMenuProcessor().get(resolvedId).mayOpen(profile)) {
                        return;
                    }
                    rpgMenu.openMenu(profile, resolvedId);
                };
            }
            case CLOSE -> RPGMenu::closeMenu;
            case UPDATE -> profile -> {
                final OpenedMenu menu = OpenedMenu.getMenu(profile);
                if (menu != null) {
                    menu.update();
                }
            };
        };
        return new OnlineActionAdapter(new MenuAction(consumer));
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
