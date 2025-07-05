package org.betonquest.betonquest.compatibility.protocollib.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int lineLength, int lineCount, int lineFillBefore, int refreshDelay, int rateLimit,
                                 String npcNameType, String npcNameAlign,
                                 boolean npcNameSeperator, boolean optionsSeperator,
                                 String controlSelect, String controlMove, String controlCancel,
                                 VariableComponent npcName, VariableComponent npcText, Component npcTextWrap,
                                 VariableComponent optionText, Component optionTextWrap,
                                 VariableComponent optionSelectedText, Component optionSelectedTextWrap,
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
        final int lineLength = config.getInt("line_length");
        final int lineCount = config.getInt("line_count");
        final int lineFillBefore = config.getInt("line_fill_before");
        final int refreshDelay = config.getInt("refresh_delay");
        final int rateLimit = config.getInt("rate_limit");

        final String npcNameType = config.getString("npc_name_type", "");
        final String npcNameAlign = config.getString("npc_name_align", "");
        final boolean npcNameSeperator = config.getBoolean("npc_name_seperator");
        final boolean optionsSeperator = config.getBoolean("options_seperator");
        final String controlSelect = config.getString("control_select", "");
        final String controlMove = config.getString("control_move", "");
        final String controlCancel = config.getString("control_cancel", "");

        final String npcName = config.getString("npc_name", "");
        final String npcText = config.getString("npc_text", "");
        final String npcTextWrap = config.getString("npc_text_wrap", "");
        final String optionText = config.getString("option_text", "");
        final String optionTextWrap = config.getString("option_text_wrap", "");
        final String optionSelectedText = config.getString("option_selected_text", "");
        final String optionSelectedTextWrap = config.getString("option_selected_text_wrap", "");
        final String scrollUp = config.getString("scroll_up", "");
        final String scrollDown = config.getString("scroll_down", "");

        return new MenuConvIOSettings(lineLength, lineCount, lineFillBefore, refreshDelay, rateLimit,
                npcNameType, npcNameAlign, npcNameSeperator, optionsSeperator, controlSelect, controlMove, controlCancel,
                new VariableComponent(messageParser.parse(npcName)), new VariableComponent(messageParser.parse(npcText)),
                messageParser.parse(npcTextWrap), new VariableComponent(messageParser.parse(optionText)),
                messageParser.parse(optionTextWrap), new VariableComponent(messageParser.parse(optionSelectedText)),
                messageParser.parse(optionSelectedTextWrap), messageParser.parse(scrollUp), messageParser.parse(scrollDown)
        );
    }
}
