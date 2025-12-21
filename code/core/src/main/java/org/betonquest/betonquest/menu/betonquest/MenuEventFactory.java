package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.OpenedMenu;
import org.betonquest.betonquest.menu.RPGMenu;

/**
 * Factory to create {@link MenuEvent}s from {@link Instruction}s.
 */
public class MenuEventFactory implements PlayerEventFactory {

    /**
     * Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * RPGMenu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Create a new factory for Menu Events.
     *
     * @param loggerFactory the factory to create new class specific logger
     * @param data          the data used for primary server access
     * @param rpgMenu       the rpg menu instance
     */
    public MenuEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final RPGMenu rpgMenu) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.rpgMenu = rpgMenu;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Operation operation = instruction.get(DefaultArgumentParsers.forEnumeration(Operation.class)).getValue(null);
        final QuestConsumer<OnlineProfile> action = switch (operation) {
            case OPEN -> {
                final Variable<MenuID> menuID = instruction.get(MenuID::new);
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
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(new MenuEvent(action),
                loggerFactory.create(MenuEvent.class), instruction.getPackage()), data);
    }

    /**
     * The action of the event.
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
