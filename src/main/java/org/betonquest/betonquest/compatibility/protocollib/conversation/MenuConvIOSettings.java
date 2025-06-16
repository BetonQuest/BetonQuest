package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int configSelectionCooldown, int configRefreshDelay, int configLineLength,
                                 int configStartNewLines, boolean configNpcNameNewlineSeparator,
                                 boolean configNpcTextFillNewLines, String configControlSelect,
                                 String configControlCancel, String configControlMove, String configNpcNameAlign,
                                 String configNpcNameType, String configNpcWrap, String configNpcText,
                                 String configNpcTextReset, String configOptionWrap, String configOptionText,
                                 String configOptionTextReset, String configOptionSelected,
                                 String configOptionSelectedReset, String configOptionSelectedWrap,
                                 String configNpcNameFormat) {

    /**
     * Creates a new instance of MenuConvIOSettings from a configuration section.
     *
     * @param config the configuration section to read settings from
     * @return a new instance of MenuConvIOSettings
     */
    public static MenuConvIOSettings fromConfigurationSection(final ConfigurationSection config) {
        final int configSelectionCooldown = config.getInt("selection_cooldown");
        final int configRefreshDelay = config.getInt("refresh_delay");
        final int configLineLength = config.getInt("line_length");
        final int configStartNewLines = config.getInt("start_new_lines");
        final boolean configNpcNameNewlineSeparator = config.getBoolean("npc_name_newline_separator");
        final boolean configNpcTextFillNewLines = config.getBoolean("npc_text_fill_new_lines");
        final String configControlSelect = config.getString("control_select", "");
        final String configControlCancel = config.getString("control_cancel", "");
        final String configControlMove = config.getString("control_move", "");
        final String configNpcNameAlign = config.getString("npc_name_align", "");
        final String configNpcNameType = config.getString("npc_name_type", "");

        final String configNpcWrap = config.getString("npc_wrap", "").replace('&', '§');
        final String configNpcText = config.getString("npc_text", "").replace('&', '§');
        final String configNpcTextReset = config.getString("npc_text_reset", "").replace('&', '§');
        final String configOptionWrap = config.getString("option_wrap", "").replace('&', '§');
        final String configOptionText = config.getString("option_text", "").replace('&', '§');
        final String configOptionTextReset = config.getString("option_text_reset", "").replace('&', '§');
        final String configOptionSelected = config.getString("option_selected", "").replace('&', '§');
        final String configOptionSelectedReset = config.getString("option_selected_reset", "").replace('&', '§');
        final String configOptionSelectedWrap = config.getString("option_selected_wrap", "").replace('&', '§');
        final String configNpcNameFormat = config.getString("npc_name_format", "").replace('&', '§');

        return new MenuConvIOSettings(configSelectionCooldown, configRefreshDelay, configLineLength,
                configStartNewLines, configNpcNameNewlineSeparator, configNpcTextFillNewLines, configControlSelect,
                configControlCancel, configControlMove, configNpcNameAlign, configNpcNameType, configNpcWrap,
                configNpcText, configNpcTextReset, configOptionWrap, configOptionText, configOptionTextReset,
                configOptionSelected, configOptionSelectedReset, configOptionSelectedWrap, configNpcNameFormat);
    }
}
