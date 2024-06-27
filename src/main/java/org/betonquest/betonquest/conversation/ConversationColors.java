package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.Config;
import org.bukkit.ChatColor;

import java.util.Locale;

/**
 * Holds the colors of the conversations
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.CommentRequired"})
public final class ConversationColors {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(ConversationColors.class);

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

    public static void loadColors() {
        try {
            final String[] text = Config.getConfigString("conversation_colors.text").split(",");
            final ChatColor[] textColors = new ChatColor[text.length];
            for (int i = 0; i < text.length; i++) {
                textColors[i] = ChatColor.valueOf(text[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            final String[] npc = Config.getConfigString("conversation_colors.npc").split(",");
            final ChatColor[] npcColors = new ChatColor[npc.length];
            for (int i = 0; i < npc.length; i++) {
                npcColors[i] = ChatColor.valueOf(npc[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            final String[] player = Config.getConfigString("conversation_colors.player").split(",");
            final ChatColor[] playerColors = new ChatColor[player.length];
            for (int i = 0; i < player.length; i++) {
                playerColors[i] = ChatColor.valueOf(player[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            final String[] number = Config.getConfigString("conversation_colors.number").split(",");
            final ChatColor[] numberColors = new ChatColor[number.length];
            for (int i = 0; i < number.length; i++) {
                numberColors[i] = ChatColor.valueOf(number[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            final String[] answer = Config.getConfigString("conversation_colors.answer").split(",");
            final ChatColor[] answerColors = new ChatColor[answer.length];
            for (int i = 0; i < answer.length; i++) {
                answerColors[i] = ChatColor.valueOf(answer[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            final String[] option = Config.getConfigString("conversation_colors.option").split(",");
            final ChatColor[] optionColors = new ChatColor[option.length];
            for (int i = 0; i < option.length; i++) {
                optionColors[i] = ChatColor.valueOf(option[i].toUpperCase(Locale.ROOT).trim().replace(" ", "_"));
            }
            colors = new Colors(textColors, npcColors, playerColors, numberColors, answerColors, optionColors);
        } catch (final IllegalArgumentException e) {
            colors = new Colors(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
            LOG.warn("Could not parse conversation colors, everything will be white!", e);
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
