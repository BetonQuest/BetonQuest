package pl.betoncraft.betonquest.conversation;

import org.bukkit.ChatColor;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Holds the colors of the conversations
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class ConversationColors {

    private static ChatColor[] npcColors;
    private static ChatColor[] playerColors;
    private static ChatColor[] textColors;
    private static ChatColor[] answerColors;
    private static ChatColor[] numberColors;
    private static ChatColor[] optionColors;

    public ConversationColors() {
        try {
            final String[] text = Config.getString("config.conversation_colors.text").split(",");
            textColors = new ChatColor[text.length];
            for (int i = 0; i < text.length; i++) {
                textColors[i] = ChatColor.valueOf(text[i].toUpperCase().trim().replace(" ", "_"));
            }
            final String[] npc = Config.getString("config.conversation_colors.npc").split(",");
            npcColors = new ChatColor[npc.length];
            for (int i = 0; i < npc.length; i++) {
                npcColors[i] = ChatColor.valueOf(npc[i].toUpperCase().trim().replace(" ", "_"));
            }
            final String[] player = Config.getString("config.conversation_colors.player").split(",");
            playerColors = new ChatColor[player.length];
            for (int i = 0; i < player.length; i++) {
                playerColors[i] = ChatColor.valueOf(player[i].toUpperCase().trim().replace(" ", "_"));
            }
            final String[] number = Config.getString("config.conversation_colors.number").split(",");
            numberColors = new ChatColor[number.length];
            for (int i = 0; i < number.length; i++) {
                numberColors[i] = ChatColor.valueOf(number[i].toUpperCase().trim().replace(" ", "_"));
            }
            final String[] answer = Config.getString("config.conversation_colors.answer").split(",");
            answerColors = new ChatColor[answer.length];
            for (int i = 0; i < answer.length; i++) {
                answerColors[i] = ChatColor.valueOf(answer[i].toUpperCase().trim().replace(" ", "_"));
            }
            final String[] option = Config.getString("config.conversation_colors.option").split(",");
            optionColors = new ChatColor[option.length];
            for (int i = 0; i < option.length; i++) {
                optionColors[i] = ChatColor.valueOf(option[i].toUpperCase().trim().replace(" ", "_"));
            }
        } catch (IllegalArgumentException e) {
            textColors = new ChatColor[]{};
            npcColors = new ChatColor[]{};
            playerColors = new ChatColor[]{};
            optionColors = new ChatColor[]{};
            answerColors = new ChatColor[]{};
            numberColors = new ChatColor[]{};
            LogUtils.getLogger().log(Level.WARNING, "Could not parse conversation colors, everything will be white!");
            LogUtils.logThrowable(e);
            return;
        }
    }

    /**
     * @return the map of conversation colors
     */
    public static HashMap<String, ChatColor[]> getColors() {
        final HashMap<String, ChatColor[]> map = new HashMap<>();
        map.put("text", textColors);
        map.put("option", optionColors);
        map.put("answer", answerColors);
        map.put("number", numberColors);
        map.put("npc", npcColors);
        map.put("player", playerColors);
        return map;
    }

}
