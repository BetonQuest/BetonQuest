package org.betonquest.betonquest.compatibility.packetevents.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 *
 * @param lineLength             maximum length of a line till its wrapped in pixels
 * @param lineCount              height of a conversation in lines
 * @param lineFillBefore         amount of empty lines before a conversation starts
 * @param refreshDelay           time interval before printing the conversation again in ticks
 * @param rateLimit              time to wait until a new option can be selected in ticks
 * @param npcNameType            place to show the NPC name, chat or none
 * @param npcNameAlign           for npc_name_type chat, the alignment of the name, left, right or center
 * @param npcNameSeparator       separate the NPC name with an empty line from the text
 * @param optionsSeparator       separate the NPC text from the player options by filling remaining space with empty lines
 * @param controlSelect          comma separated actions to select an option, jump, left_click or sneak
 * @param controlMove            comma separated actions to move the selection, move or scroll
 * @param controlCancel          comma separated actions to cancel the conversation, jump, left_click or sneak
 * @param npcName                the format of the NPC name, placeholder {npc_name}
 * @param npcText                the format of the NPC text, placeholder {npc_text}
 * @param npcTextWrap            prefix that gets applied to the start of a new line if the actual text is too long
 * @param optionText             the format of the player options, placeholder {option_text}
 * @param optionTextWrap         prefix that gets applied to the start of a new line if the actual text is too long
 * @param optionSelectedText     format of the selected player option, placeholder {option_text}
 * @param optionSelectedTextWrap prefix that gets applied to the start of a new line if the actual text is too long
 * @param scrollUp               arrow format to scroll up
 * @param scrollDown             arrow format to scroll down
 */
public record MenuConvIOSettings(int lineLength, int lineCount, int lineFillBefore, int refreshDelay, int rateLimit,
                                 String npcNameType, String npcNameAlign,
                                 boolean npcNameSeparator, boolean optionsSeparator,
                                 String controlSelect, String controlMove, String controlCancel,
                                 VariableComponent npcName, VariableComponent npcText, Component npcTextWrap,
                                 VariableComponent optionText, Component optionTextWrap,
                                 VariableComponent optionSelectedText, Component optionSelectedTextWrap,
                                 Component scrollUp, Component scrollDown) {

    /**
     * Creates a new instance of MenuConvIOSettings from a configuration section.
     *
     * @param textParser the text parser to use to parse components
     * @param config     the configuration section to read settings from
     * @return a new instance of MenuConvIOSettings
     * @throws QuestException if the text parser could not parse a text
     */
    public static MenuConvIOSettings fromConfigurationSection(final TextParser textParser, final ConfigurationSection config) throws QuestException {
        final int lineLength = config.getInt("line_length");
        final int lineCount = config.getInt("line_count");
        final int lineFillBefore = config.getInt("line_fill_before");
        final int refreshDelay = config.getInt("refresh_delay");
        final int rateLimit = config.getInt("rate_limit");

        final String npcNameType = config.getString("npc_name_type", "");
        final String npcNameAlign = config.getString("npc_name_align", "");
        final boolean npcNameSeparator = config.getBoolean("npc_name_separator");
        final boolean optionsSeparator = config.getBoolean("options_separator");
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
                npcNameType, npcNameAlign, npcNameSeparator, optionsSeparator, controlSelect, controlMove, controlCancel,
                new VariableComponent(textParser.parse(npcName)), new VariableComponent(textParser.parse(npcText)),
                textParser.parse(npcTextWrap), new VariableComponent(textParser.parse(optionText)),
                textParser.parse(optionTextWrap), new VariableComponent(textParser.parse(optionSelectedText)),
                textParser.parse(optionSelectedTextWrap), textParser.parse(scrollUp), textParser.parse(scrollDown)
        );
    }
}
