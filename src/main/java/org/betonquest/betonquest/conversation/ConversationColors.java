package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Holds the colors of the conversations.
 */
public final class ConversationColors {
    /**
     * The empty fallback ChatColors.
     */
    private static final ChatColor[] EMPTY = {};

    /**
     * Stored Conversation Colors.
     */
    private static Colors colors = new Colors(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);

    private ConversationColors() {
    }

    /**
     * Loads the conversation colors.
     *
     * @param log    the custom logger used when the config contains error
     * @param config the config to load the colors from
     */
    public static void loadColors(final BetonQuestLogger log, final FileConfigAccessor config) {
        try {
            final ConfigurationSection section = config.getConfigurationSection("conversation.color");
            if (section == null) {
                log.warn("Conversation colors do not exist in the config, everything will be white!");
                return;
            }
            final String[] sections = {"text", "npc", "player", "number", "answer", "option"};
            final ChatColor[][] rawColors = new ChatColor[sections.length][];
            for (int k = 0; k < sections.length; k++) {
                final String colorString = section.getString(sections[k]);
                if (colorString == null) {
                    log.warn("Conversation color " + sections[k] + " does not exist in the config, it will be white!");
                    continue;
                }
                final String[] text = colorString.split(",");
                final ChatColor[] textColors = new ChatColor[text.length];
                for (int i = 0; i < text.length; i++) {
                    textColors[i] = ChatColor.valueOf(text[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
                }
                rawColors[k] = textColors;
            }
            colors = new Colors(rawColors[0], rawColors[1], rawColors[2], rawColors[3], rawColors[4], rawColors[5]);
        } catch (final IllegalArgumentException e) {
            colors = new Colors(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
            log.warn("Could not parse conversation colors, everything will be white!", e);
        }
    }

    /**
     * Gets the conversation colors.
     *
     * @return the record with the stored colors
     */
    public static Colors getColors() {
        return colors;
    }

    public record Colors(ChatColor[] text, ChatColor[] npc, ChatColor[] player, ChatColor[] number, ChatColor[] answer,
                         ChatColor[] option) {
    }
}
