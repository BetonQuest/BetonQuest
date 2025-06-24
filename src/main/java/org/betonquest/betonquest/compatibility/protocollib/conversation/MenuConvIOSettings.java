package org.betonquest.betonquest.compatibility.protocollib.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int selectionCooldown, int refreshDelay, int lineLength, int startNewLines,
                                 boolean npcNameNewlineSeparator, boolean npcTextFillNewLines, String controlSelect,
                                 String controlCancel, String controlMove, String npcNameAlign, String npcNameType,
                                 Component npcWrap, VariableComponent npcText, Component optionWrap,
                                 VariableComponent optionText, VariableComponent optionSelected,
                                 Component optionSelectedWrap, VariableComponent npcNameFormat,
                                 Component scrollUp, Component scrollDown) {

    /**
     * Creates a new instance of MenuConvIOSettings from a configuration section.
     *
     * @param messageParser the message parser to use to parse components
     * @param config        the configuration section to read settings from
     * @return a new instance of MenuConvIOSettings
     * @throws QuestException if the message parser could not parse a message
     */
    public static MenuConvIOSettings fromConfigurationSection(final MessageParser messageParser, final ConfigurationSection config) throws QuestException {
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

        final String npcWrap = config.getString("npc_wrap", "");
        final String npcText = config.getString("npc_text", "");
        final String optionWrap = config.getString("option_wrap", "");
        final String optionText = config.getString("option_text", "");
        final String optionSelected = config.getString("option_selected", "");
        final String optionSelectedWrap = config.getString("option_selected_wrap", "");
        final String npcNameFormat = config.getString("npc_name_format", "");
        final String scrollUp = config.getString("scroll_up", "");
        final String scrollDown = config.getString("scroll_down", "");

        return new MenuConvIOSettings(selectionCooldown, refreshDelay, lineLength, startNewLines, npcNameNewlineSeparator,
                npcTextFillNewLines, controlSelect, controlCancel, controlMove, npcNameAlign, npcNameType,
                messageParser.parse(npcWrap),
                new VariableComponent(messageParser.parse(npcText)),
                messageParser.parse(optionWrap),
                new VariableComponent(messageParser.parse(optionText)),
                new VariableComponent(messageParser.parse(optionSelected)),
                messageParser.parse(optionSelectedWrap),
                new VariableComponent(messageParser.parse(npcNameFormat)),
                messageParser.parse(scrollUp),
                messageParser.parse(scrollDown));
    }
}
