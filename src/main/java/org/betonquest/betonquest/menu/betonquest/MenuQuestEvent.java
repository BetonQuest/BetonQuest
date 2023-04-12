package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;

/**
 * Event to open or close menus
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuQuestEvent extends QuestEvent {
    private final Operation operation;

    private MenuID menu;

    public MenuQuestEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.operation = instruction.getEnum(Operation.class);
        if (this.operation == Operation.OPEN) {
            try {
                this.menu = new MenuID(instruction.getPackage(), instruction.next());
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error while parsing 2 argument: Error while loading menu: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public Void execute(final Profile profile) throws QuestRuntimeException {
        if (operation == Operation.OPEN) {
            BetonQuest.getInstance().getRpgMenu().openMenu(profile.getOnlineProfile().get(), menu);
        } else {
            RPGMenu.closeMenu(profile.getOnlineProfile().get());
        }
        return null;
    }

    public enum Operation {
        OPEN,
        CLOSE
    }
}
