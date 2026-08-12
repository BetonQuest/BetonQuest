package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Holds configuration settings for the Dialog conversation io.
 *
 * @param questerInTitle      whether the npc's name will be shown in dialog title or in the text
 * @param onlyButtons         whether the text should be displayed only on the buttons or also in it own boxes
 * @param buttonRenderPadding the padding used when rendering buttons
 * @param buttonWidth         the minimum width for buttons
 * @param closeWithEscape     whether the dialog can be closed using the Escape key
 * @param closeButtonEnabled  whether the close button is enabled
 */
public record DialogSettings(
        boolean questerInTitle,
        boolean onlyButtons,
        int buttonRenderPadding,
        int buttonWidth,
        boolean closeWithEscape,
        boolean closeButtonEnabled
) {

    /**
     * Constructs a new DialogSettings from the specified configuration section.
     *
     * @param section the configuration section containing dialog settings
     * @return the settings from the configuration setting
     */
    public static DialogSettings fromSection(final ConfigurationSection section) {
        final boolean questerInTitle = section.getBoolean("quester_in_title", true);
        final boolean onlyButtons = section.getBoolean("only_buttons", false);

        final int buttonRenderPadding = section.getInt("button_render_padding", 13);
        final int defaultButtonWidth = section.getInt("button_width", 250);

        final boolean closeButtonEnabled = section.getBoolean("close_button_enabled", true);
        final boolean closeWithEscape = section.getBoolean("close_with_escape", true);
        return new DialogSettings(questerInTitle, onlyButtons, buttonRenderPadding, defaultButtonWidth, closeButtonEnabled, closeWithEscape);
    }
}
