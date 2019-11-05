/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conversation;

import org.bukkit.ChatColor;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Holds the colors of the conversations
 *
 * @author Jakub Sapalski
 */
public class ConversationColors {

    private static ChatColor[] npcColors;
    private static ChatColor[] playerColors;
    private static ChatColor[] textColors;
    private static ChatColor[] answerColors;
    private static ChatColor[] numberColors;
    private static ChatColor[] optionColors;

    public ConversationColors() {
        try {
            String[] text = Config.getString("config.conversation_colors.text").split(",");
            textColors = new ChatColor[text.length];
            for (int i = 0; i < text.length; i++) {
                textColors[i] = ChatColor.valueOf(text[i].toUpperCase().trim().replace(" ", "_"));
            }
            String[] npc = Config.getString("config.conversation_colors.npc").split(",");
            npcColors = new ChatColor[npc.length];
            for (int i = 0; i < npc.length; i++) {
                npcColors[i] = ChatColor.valueOf(npc[i].toUpperCase().trim().replace(" ", "_"));
            }
            String[] player = Config.getString("config.conversation_colors.player").split(",");
            playerColors = new ChatColor[player.length];
            for (int i = 0; i < player.length; i++) {
                playerColors[i] = ChatColor.valueOf(player[i].toUpperCase().trim().replace(" ", "_"));
            }
            String[] number = Config.getString("config.conversation_colors.number").split(",");
            numberColors = new ChatColor[number.length];
            for (int i = 0; i < number.length; i++) {
                numberColors[i] = ChatColor.valueOf(number[i].toUpperCase().trim().replace(" ", "_"));
            }
            String[] answer = Config.getString("config.conversation_colors.answer").split(",");
            answerColors = new ChatColor[answer.length];
            for (int i = 0; i < answer.length; i++) {
                answerColors[i] = ChatColor.valueOf(answer[i].toUpperCase().trim().replace(" ", "_"));
            }
            String[] option = Config.getString("config.conversation_colors.option").split(",");
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
        HashMap<String, ChatColor[]> map = new HashMap<>();
        map.put("text", textColors);
        map.put("option", optionColors);
        map.put("answer", answerColors);
        map.put("number", numberColors);
        map.put("npc", npcColors);
        map.put("player", playerColors);
        return map;
    }

}
