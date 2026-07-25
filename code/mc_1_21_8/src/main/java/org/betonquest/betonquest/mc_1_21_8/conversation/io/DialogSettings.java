package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Holds configuration settings for the Dialog conversation io.
 *
 * @param layout              the layout type for the dialog
 * @param buttonRenderPadding the padding used when rendering buttons
 * @param buttonWidth         the minimum width for buttons
 * @param closeWithEscape     whether the dialog can be closed using the Escape key
 * @param closeButtonEnabled  whether the close button is enabled
 * @param closeButtonText     the text displayed on the close button
 * @param closeButtonWidth    the width of the close button
 */
public record DialogSettings(
        DialogLayout layout,
        int buttonRenderPadding,
        int buttonWidth,
        boolean closeWithEscape,
        boolean closeButtonEnabled,
        Component closeButtonText,
        int closeButtonWidth
) {

    /**
     * Constructs a new DialogSettings from the specified configuration section.
     *
     * @param textParser the text parser used to parse text
     * @param section    the configuration section containing dialog settings, or null for defaults
     * @return the settings from the configuration setting
     * @throws QuestException if the configuration contains invalid dialog settings
     */
    public static DialogSettings fromSection(final TextParser textParser, @Nullable final ConfigurationSection section) throws QuestException {
        if (section == null) {
            return new DialogSettings(DialogLayout.NPC_TITLE, 13, 250, true, true, textParser.parse("<red>close"), 250);
        }
        final DialogLayout layout = new EnumParser<>(DialogLayout.class).apply(section.getString("layout", "NPC_TITLE"));

        final int buttonRenderPadding = section.getInt("button-render-padding", 13);
        final int defaultButtonWidth = section.getInt("button-width", 250);

        final ConfigurationSection close = Objects.requireNonNullElseGet(section.getConfigurationSection("close-button"), YamlConfiguration::new);
        final boolean closeButtonEnabled = close.getBoolean("enabled", true);
        final Component closeButtonText = textParser.parse(close.getString("text", "<red>Close"));
        final int closeButtonWidth = close.getInt("width", 250);
        final boolean closeWithEscape = close.getBoolean("close-with-escape", true);
        return new DialogSettings(layout, buttonRenderPadding, defaultButtonWidth, closeButtonEnabled, closeWithEscape,
                closeButtonText, closeButtonWidth);
    }
}
