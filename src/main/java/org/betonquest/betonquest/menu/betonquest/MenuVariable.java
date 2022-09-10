package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.menu.OpenedMenu;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * Returns the title of the players currently opened menu
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuVariable extends Variable {

    public MenuVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public String getValue(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        final OpenedMenu menu = OpenedMenu.getMenu(player);
        if (menu == null) {
            return "";
        }
        return menu.getData().getTitle(playerID);
    }
}
