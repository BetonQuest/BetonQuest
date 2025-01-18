package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.menu.OpenedMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Returns the title of the players currently opened menu
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuVariable extends Variable {

    public MenuVariable(final Instruction instruction) throws QuestException {
        super(instruction);
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        final OpenedMenu menu = OpenedMenu.getMenu(profile.getOnlineProfile().get());
        if (menu == null) {
            return "";
        }
        return menu.getData().getTitle(profile);
    }
}
