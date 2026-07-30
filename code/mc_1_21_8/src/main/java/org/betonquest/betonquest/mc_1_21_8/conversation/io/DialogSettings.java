package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * Holds configuration settings for the Dialog conversation io.
 *
 * @param layout              the layout type for the dialog
 * @param buttonRenderPadding the padding used when rendering buttons
 * @param buttonWidth         the minimum width for buttons
 * @param closeWithEscape     whether the dialog can be closed using the Escape key
 * @param closeButtonEnabled  whether the close button is enabled
 */
public record DialogSettings(
        DialogLayout layout,
        int buttonRenderPadding,
        int buttonWidth,
        boolean closeWithEscape,
        boolean closeButtonEnabled
) {

    /**
     * Constructs a new DialogSettings from the specified configuration section.
     *
     * @param section the configuration section containing dialog settings, or null for defaults
     * @return the settings from the configuration setting
     * @throws QuestException if the configuration contains invalid dialog settings
     */
    public static DialogSettings fromSection(@Nullable final ConfigurationSection section) throws QuestException {
        if (section == null) {
            return new DialogSettings(DialogLayout.NPC_TITLE, 13, 250, true, true);
        }
        final DialogLayout layout = new EnumParser<>(DialogLayout.class).apply(section.getString("layout", "NPC_TITLE"));

        final int buttonRenderPadding = section.getInt("button-render-padding", 13);
        final int defaultButtonWidth = section.getInt("button-width", 250);

        final boolean closeButtonEnabled = section.getBoolean("close-button-enabled", true);
        final boolean closeWithEscape = section.getBoolean("close-with-escape", true);
        return new DialogSettings(layout, buttonRenderPadding, defaultButtonWidth, closeButtonEnabled, closeWithEscape);
    }
}
