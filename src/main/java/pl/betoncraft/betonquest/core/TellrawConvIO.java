/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


/**
 * Adds tellraw command handling to the SimpleConvIO
 * 
 * @author Jakub Sapalski
 */
public class TellrawConvIO extends SimpleConvIO implements Listener {
    
    private HashMap<Integer, String> hashes;
    private int count = 0;
    private String color = "white";
    private boolean italic = false;
    private boolean bold = false;
    private boolean underline = false;

    public TellrawConvIO(Conversation conv, String playerID, String npcName) {
        super(conv, playerID, npcName);
        hashes = new HashMap<>();
        char[] prefix = playerFormat.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        for (int j = 0; j < prefix.length; j++) {
            if (prefix[j] == '§') {
                char[] temp = new char[]{prefix[j], prefix[j+1]};
                list.add(new String(temp));
            }
        }
        if (list.size() < 2) {
            return;
        }
        for (int j = 0; j < 2; j++) {
            String code = list.get(list.size() + j - 2);
            switch (code.toLowerCase().substring(1, 2)) {
                case "0":
                    color = "black";
                    break;
                case "1":
                    color = "dark_blue";
                    break;
                case "2":
                    color = "dark_aqua";
                    break;
                case "3":
                    color = "dark_green";
                    break;
                case "4":
                    color = "dark_red";
                    break;
                case "5":
                    color = "dark_purple";
                    break;
                case "6":
                    color = "gold";
                    break;
                case "7":
                    color = "grey";
                    break;
                case "8":
                    color = "dark_grey";
                    break;
                case "9":
                    color = "blue";
                    break;
                case "a":
                    color = "green";
                    break;
                case "b":
                    color = "aqua";
                    break;
                case "c":
                    color = "red";
                    break;
                case "d":
                    color = "light_purple";
                    break;
                case "e":
                    color = "yellow";
                    break;
                case "f":
                    color = "white";
                    break;
                case "o":
                    italic = true;
                    break;
                case "l":
                    bold = true;
                    break;
                case "n":
                    underline = true;
                    break;
            }
        }
    }
    
    @EventHandler
    public void onCommandAnswer(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) return;
        if (!event.getMessage().toLowerCase().startsWith("/betonquestanswer ")) return;
        event.setCancelled(true);
        String[] parts = event.getMessage().split(" ");
        if (parts.length != 2) return;
        String hash = parts[1];
        for (int j = 1; j <= hashes.size(); j++) {
            if (hashes.get(j).equals(hash)) {
                player.sendMessage(answerFormat + options.get(j));
                conv.passPlayerAnswer(j);
                return;
            }
        }
    }

    @Override
    public void display() {
        player.sendMessage(npcFormat + npcText);
        for (int j = 1; j <= options.size(); j++) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + name + " [{\"text\":\"" + playerFormat.replace("%number%", Integer.toString(j)) + "\"},{\"text\":\"" + options.get(j) + "\",\"color\":\"" + color + "\",\"bold\":\"" + bold + "\",\"italic\":\"" + italic + "\",\"underlined\":\"" + underline + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/betonquestanswer " + hashes.get(j) + "\"}}]");
        }
    }
    
    @Override
    public void addPlayerOption(String option) {
        super.addPlayerOption(option);
        count++;
        hashes.put(count, UUID.randomUUID().toString());
    }
    
    @Override
    public void clear() {
        super.clear();
        hashes.clear();
        count = 0;
    }
}
