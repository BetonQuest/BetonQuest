package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import java.util.Locale;

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
     */
    /* default */ DialogSettings(final ConfigAccessor section) {

        final String layoutStr = section.getString("layout", "NPC_TITLE").toUpperCase(Locale.ROOT);
        DialogLayout parsedLayout;
        try {
            parsedLayout = DialogLayout.valueOf(layoutStr);
        } catch (final IllegalArgumentException e) {
            parsedLayout = DialogLayout.NPC_TITLE;
        }
        this.layout = parsedLayout;

        this.buttonRenderPadding = section.getInt("button-render-padding", 13);
        this.defaultButtonWidth = section.getInt("default-button-width", 250);

        this.closeButtonEnabled = section.getBoolean("close-button.enabled", true);
        this.closeButtonText = section.getString("close-button.text", "<red>Close");
        this.closeButtonWidth = section.getInt("close-button.width", 250);
        this.closeWithEscape = section.getBoolean("close-with-escape", true);
    }
}
