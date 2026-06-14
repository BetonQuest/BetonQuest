package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Holds configuration settings for the Dialog conversation interface.
 */
class DialogSettings {

    /** The layout type for the dialog. */
    /* default */ final DialogLayout layout;

    /** The padding used when rendering buttons. */
    /* default */ final int buttonRenderPadding;

    /** The default width for buttons. */
    /* default */ final int defaultButtonWidth;

    /** Whether the close button is enabled. */
    /* default */ final boolean closeButtonEnabled;

    /** The text displayed on the close button. */
    /* default */ final String closeButtonText;

    /** The width of the close button. */
    /* default */ final int closeButtonWidth;

    /** Whether the dialog can be closed using the Escape key. */
    /* default */ final boolean closeWithEscape;

    /**
     * Constructs a new DialogSettings from the specified configuration section.
     *
     * @param section the configuration section containing dialog settings, or null for defaults
     * @throws QuestException if the configuration contains invalid dialog settings
     */
    /* default */ DialogSettings(final ConfigurationSection section) throws QuestException {
        final String layoutStr = section.getString("layout", "NPC_TITLE");
        this.layout = DialogLayout.fromString(layoutStr);

        this.buttonRenderPadding = section.getInt("button-render-padding", 13);
        this.defaultButtonWidth = section.getInt("default-button-width", 250);

        final ConfigurationSection close = section.getConfigurationSection("close-button");
        this.closeButtonEnabled = close.getBoolean("enabled", true);
        this.closeButtonText = close.getString("text", "<red>Close");
        this.closeButtonWidth = close.getInt("width", 250);
        this.closeWithEscape = close.getBoolean("close-with-escape", true);
    }
}
