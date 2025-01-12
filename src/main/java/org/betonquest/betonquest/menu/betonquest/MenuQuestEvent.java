package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;

import java.util.function.Consumer;

/**
 * Event to open or close menus
 */
public class MenuQuestEvent extends QuestEvent {
    /**
     * The stuff to do with the profile.
     */
    private final Consumer<OnlineProfile> doStuff;

    /**
     * Creates a new MenuQuestEvent.
     *
     * @param instruction the instruction to parse
     * @throws QuestException if the instruction is invalid
     */
    public MenuQuestEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        final Operation operation = instruction.getEnum(Operation.class);
        if (operation == Operation.OPEN) {
            try {
                final MenuID menu = new MenuID(instruction.getPackage(), instruction.next());
                doStuff = profile -> BetonQuest.getInstance().getRpgMenu().openMenu(profile, menu);
            } catch (final ObjectNotFoundException e) {
                throw new QuestException("Error while parsing 2 argument: Error while loading menu: " + e.getMessage(), e);
            }
        } else {
            doStuff = RPGMenu::closeMenu;
        }
    }

    @Override
    public Void execute(final Profile profile) throws QuestException {
        doStuff.accept(profile.getOnlineProfile().get());
        return null;
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
