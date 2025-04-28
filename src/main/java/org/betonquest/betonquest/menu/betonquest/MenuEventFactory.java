package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

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
        final Operation operation = instruction.get(Argument.ENUM(Operation.class)).getValue(null);
        final Variable<MenuID> menuID = operation == Operation.OPEN ? instruction.get(MenuID::new) : null;
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(new MenuEvent(rpgMenu, menuID),
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
        CLOSE
    }
}
