package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Stream;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * the message parser to parse the configuration messages.
     */
    private final MessageParser messageParser;

    /**
     * The font registry used for the conversation.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Menu conversation IO factory.
     *
     * @param messageParser the message parser to parse the configuration messages
     * @param fontRegistry  the font registry used for the conversation
     * @param colors        the colors used for the conversation
     */
    public MenuConvIOFactory(final MessageParser messageParser, final FontRegistry fontRegistry, final ConversationColors colors) {
        this.messageParser = messageParser;
        this.fontRegistry = fontRegistry;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = getSettings(conversation.getPackage());
        final ComponentLineWrapper componentLineWrapper = new ComponentLineWrapper(fontRegistry, settings.configLineLength());
        return new MenuConvIO(conversation, onlineProfile, colors, settings, componentLineWrapper);
    }

    private MenuConvIOSettings getSettings(final QuestPackage conversationPack) throws QuestException {
        int configSelectionCooldown = 10;
        int configRefreshDelay = 180;
        int configLineLength = 320;
        int configStartNewLines = 10;
        boolean configNpcNameNewlineSeparator = true;
        boolean configNpcTextFillNewLines = true;
        String configControlSelect = "jump,left_click";
        String configControlCancel = "sneak";
        String configControlMove = "scroll,move";
        String configNpcNameAlign = "center";
        String configNpcNameType = "chat";

        String configNpcWrap = "@[legacy]&l &r";
        String configNpcText = "@[legacy]&l &r&f{npc_text}";
        String configOptionWrap = "@[legacy]&r&l &l &l &l &r";
        String configOptionText = "@[legacy]&l &l &l &l &r&8[ &b{option_text}&8 ]";
        String configOptionSelected = "@[legacy]&l &r &r&7»&r &8[ &f&n{option_text}&8 ]";
        String configOptionSelectedWrap = "@[legacy]&r&l &l &l &l &r&f&n";
        String configNpcNameFormat = "@[legacy]&e{npc_name}&r";

        for (final QuestPackage pack : Stream.concat(
                BetonQuest.getInstance().getPackages().values().stream().filter(p -> !p.equals(conversationPack)),
                Stream.of(conversationPack)).toList()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("menu_conv_io");
            if (section == null) {
                continue;
            }

            configSelectionCooldown = section.getInt("selectionCooldown", configSelectionCooldown);
            configRefreshDelay = section.getInt("refresh_delay", configRefreshDelay);
            configLineLength = section.getInt("line_length", configLineLength);
            configStartNewLines = section.getInt("start_new_lines", configStartNewLines);
            configNpcNameNewlineSeparator = section.getBoolean("npc_name_newline_separator", configNpcNameNewlineSeparator);
            configNpcTextFillNewLines = section.getBoolean("npc_text_fill_new_lines", configNpcTextFillNewLines);
            configControlSelect = section.getString("control_select", configControlSelect);
            configControlCancel = section.getString("control_cancel", configControlCancel);
            configControlMove = section.getString("control_move", configControlMove);
            configNpcNameAlign = section.getString("npc_name_align", configNpcNameAlign);
            configNpcNameType = section.getString("npc_name_type", configNpcNameType);

            configNpcWrap = section.getString("npc_wrap", configNpcWrap);
            configNpcText = section.getString("npc_text", configNpcText);
            configOptionWrap = section.getString("option_wrap", configOptionWrap);
            configOptionText = section.getString("option_text", configOptionText);
            configOptionSelected = section.getString("option_selected", configOptionSelected);
            configOptionSelectedWrap = section.getString("option_selected_wrap", configOptionWrap);
            configNpcNameFormat = section.getString("npc_name_format", configNpcNameFormat);
        }

        return new MenuConvIOSettings(configSelectionCooldown, configRefreshDelay, configLineLength, configStartNewLines,
                configNpcNameNewlineSeparator, configNpcTextFillNewLines, configControlSelect, configControlCancel,
                configControlMove, configNpcNameAlign, configNpcNameType,
                messageParser.parse(configNpcWrap),
                new VariableComponent(messageParser.parse(configNpcText)),
                messageParser.parse(configOptionWrap),
                new VariableComponent(messageParser.parse(configOptionText)),
                new VariableComponent(messageParser.parse(configOptionSelected)),
                messageParser.parse(configOptionSelectedWrap),
                new VariableComponent(messageParser.parse(configNpcNameFormat)));
    }
}
