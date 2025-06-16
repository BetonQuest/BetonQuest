package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int selectionCooldown, int refreshDelay, int lineLength, int startNewLines,
                                 boolean npcNameNewlineSeparator, boolean npcTextFillNewLines, String controlSelect,
                                 String controlCancel, String controlMove, String npcNameAlign, String npcNameType,
                                 String npcWrap, String npcText, String npcTextReset, String optionWrap,
                                 String optionText, String textReset, String optionSelected, String optionSelectedReset,
                                 String optionSelectedWrap, String npcNameFormat) {

    /**
     * Creates a new instance of MenuConvIOSettings from a configuration section.
     *
     * @param config the configuration section to read settings from
     * @return a new instance of MenuConvIOSettings
     */
    public static MenuConvIOSettings fromConfigurationSection(final ConfigurationSection config) {
        final int selectionCooldown = config.getInt("selection_cooldown");
        final int refreshDelay = config.getInt("refresh_delay");
        final int lineLength = config.getInt("line_length");
        final int startNewLines = config.getInt("start_new_lines");
        final boolean npcNameNewlineSeparator = config.getBoolean("npc_name_newline_separator");
        final boolean npcTextFillNewLines = config.getBoolean("npc_text_fill_new_lines");
        final String controlSelect = config.getString("control_select", "");
        final String controlCancel = config.getString("control_cancel", "");
        final String controlMove = config.getString("control_move", "");
        final String npcNameAlign = config.getString("npc_name_align", "");
        final String npcNameType = config.getString("npc_name_type", "");

        final String npcWrap = config.getString("npc_wrap", "").replace('&', '§');
        final String npcText = config.getString("npc_text", "").replace('&', '§');
        final String npcTextReset = config.getString("npc_text_reset", "").replace('&', '§');
        final String optionWrap = config.getString("option_wrap", "").replace('&', '§');
        final String optionText = config.getString("option_text", "").replace('&', '§');
        final String optionTextReset = config.getString("option_text_reset", "").replace('&', '§');
        final String optionSelected = config.getString("option_selected", "").replace('&', '§');
        final String optionSelectedReset = config.getString("option_selected_reset", "").replace('&', '§');
        final String optionSelectedWrap = config.getString("option_selected_wrap", "").replace('&', '§');
        final String npcNameFormat = config.getString("npc_name_format", "").replace('&', '§');

        return new MenuConvIOSettings(selectionCooldown, refreshDelay, lineLength, startNewLines,
                npcNameNewlineSeparator, npcTextFillNewLines, controlSelect, controlCancel, controlMove, npcNameAlign,
                npcNameType, npcWrap, npcText, npcTextReset, optionWrap, optionText, optionTextReset, optionSelected,
                optionSelectedReset, optionSelectedWrap, npcNameFormat);
    }
}
