package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.betonquest.betonquest.api.QuestException;
import java.util.Locale;

/**
 * Represents the available layouts for the Dialog conversation IO.
 */
enum DialogLayout {

    /**
     * Layout where the NPC's name is displayed as the title of the dialog box.
     */
    NPC_TITLE,

    /**
     * Layout where the NPC's name and text are displayed together in the body.
     */
    FULL_BODY;

    /**
     * Parses a string into a DialogLayout enum.
     *
     * @param layoutStr the layout string to parse
     * @return the matching DialogLayout
     * @throws QuestException if the layout string does not match any valid enum value
     */
    /* default */ static DialogLayout fromString(final String layoutStr) throws QuestException {
        try {
            return DialogLayout.valueOf(layoutStr.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid dialog layout configuration: " + layoutStr, e);
        }
    }
}
