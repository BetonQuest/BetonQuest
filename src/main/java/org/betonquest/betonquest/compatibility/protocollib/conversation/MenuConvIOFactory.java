package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Stream;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * Create a new Menu conversation IO factory.
     */
    public MenuConvIOFactory() {
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = getSettings(conversation.getPackage());

        return new MenuConvIO(conversation, onlineProfile, settings);
    }

    private MenuConvIOSettings getSettings(final QuestPackage conversationPack) {
        int configSelectionCooldown = 10;
        int configRefreshDelay = 180;
        int configLineLength = 50;
        int configStartNewLines = 10;
        boolean configNpcNameNewlineSeparator = true;
        boolean configNpcTextFillNewLines = true;
        String configControlSelect = "jump,left_click";
        String configControlCancel = "sneak";
        String configControlMove = "scroll,move";
        String configNpcNameAlign = "center";
        String configNpcNameType = "chat";

        String configNpcWrap = "&l &r";
        String configNpcText = "&l &r&f{npc_text}";
        String configNpcTextReset = "&f";
        String configOptionWrap = "&r&l &l &l &l &r";
        String configOptionText = "&l &l &l &l &r&8[ &b{option_text}&8 ]";
        String configOptionTextReset = "&b";
        String configOptionSelected = "&l &r &r&7»&r &8[ &f&n{option_text}&8 ]";
        String configOptionSelectedReset = "&f";
        String configOptionSelectedWrap = "&r&l &l &l &l &r&f&n";
        String configNpcNameFormat = "&e{npc_name}&r";

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
            configNpcTextReset = section.getString("npc_text_reset", configNpcTextReset);
            configOptionWrap = section.getString("option_wrap", configOptionWrap);
            configOptionText = section.getString("option_text", configOptionText);
            configOptionTextReset = section.getString("option_text_reset", configOptionTextReset);
            configOptionSelected = section.getString("option_selected", configOptionSelected);
            configOptionSelectedReset = section.getString("option_selected_reset", configOptionSelectedReset);
            configOptionSelectedWrap = section.getString("option_selected_wrap", configOptionWrap);
            configNpcNameFormat = section.getString("npc_name_format", configNpcNameFormat);
        }

        return new MenuConvIOSettings(configSelectionCooldown, configRefreshDelay, configLineLength, configStartNewLines,
                configNpcNameNewlineSeparator, configNpcTextFillNewLines, configControlSelect, configControlCancel,
                configControlMove, configNpcNameAlign, configNpcNameType,
                configNpcWrap.replace('&', '§'),
                configNpcText.replace('&', '§'),
                configNpcTextReset.replace('&', '§'),
                configOptionWrap.replace('&', '§'),
                configOptionText.replace('&', '§'),
                configOptionTextReset.replace('&', '§'),
                configOptionSelected.replace('&', '§'),
                configOptionSelectedReset.replace('&', '§'),
                configOptionSelectedWrap.replace('&', '§'),
                configNpcNameFormat.replace('&', '§'));
    }
}
