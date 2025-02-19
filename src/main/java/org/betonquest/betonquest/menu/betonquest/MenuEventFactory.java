package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MenuQuestEvent}s from {@link Instruction}s.
 */
public class MenuEventFactory implements EventFactory {
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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final Operation operation = instruction.getEnum(Operation.class);
        final MenuID menuID = operation == Operation.OPEN ? instruction.getID(MenuID::new) : null;
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(new MenuQuestEvent(rpgMenu, menuID),
                loggerFactory.create(MenuQuestEvent.class), instruction.getPackage()), data);
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
