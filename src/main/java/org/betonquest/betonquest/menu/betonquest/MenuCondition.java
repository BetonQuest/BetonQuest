package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * Checks if a player has opened a menu
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuCondition extends Condition {

    private final MenuID menu;

    public MenuCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String menuID = instruction.getOptional("id");
        try {
            this.menu = (menuID == null) ? null : new MenuID(instruction.getPackage(), menuID);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Error while parsing id optional: Error while loading menu: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean execute(final String playerId) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerId);
        return RPGMenu.hasOpenedMenu(player, menu);
    }
}
