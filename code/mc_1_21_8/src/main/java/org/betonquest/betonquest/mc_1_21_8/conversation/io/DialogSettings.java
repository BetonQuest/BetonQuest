package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.bukkit.configuration.ConfigurationSection;

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
    /* default */ DialogSettings(final ConfigurationSection section) {
        if (section == null) {
            this.layout = DialogLayout.NPC_TITLE;
            this.buttonRenderPadding = 13;
            this.defaultButtonWidth = 250;
            this.closeButtonEnabled = true;
            this.closeButtonText = "<red>Close";
            this.closeButtonWidth = 250;
            this.closeWithEscape = true;
            return;
        }

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

        final ConfigurationSection close = section.getConfigurationSection("close-button");
        this.closeButtonEnabled = close == null || close.getBoolean("enabled", true);
        this.closeButtonText = close != null ? close.getString("text", "<red>Close") : "<red>Close";
        this.closeButtonWidth = close != null ? close.getInt("width", 250) : 250;
        this.closeWithEscape = close == null || close.getBoolean("close-with-escape", true);
    }
}
