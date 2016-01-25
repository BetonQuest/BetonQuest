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
package pl.betoncraft.betonquest.conversation;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Inventory GUI for conversations
 * 
 * @author Jakub Sapalski
 */
public class InventoryConvIO implements Listener, ConversationIO {
    
    private String response = null;
    private HashMap<Integer, String> options = new HashMap<>();
    private int i = 0;
    private String npcName;
    private String npcNameColor;
    private String npcTextColor;
    private String numberFormat;
    private String optionColor;
    private String answerPrefix;
    private Conversation conv;
    private Player player;
    private Inventory inv;
    private boolean allowClose = false;;
    private Location loc;
    
    public InventoryConvIO(Conversation conv, String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
        HashMap<String, ChatColor[]> colors = ConversationColors.getColors();
        StringBuilder string = new StringBuilder();
        for (ChatColor color : colors.get("npc")) {
            string.append(color);
        }
        this.npcNameColor = string.toString();
        string = new StringBuilder();
        for (ChatColor color : colors.get("text")) {
            string.append(color);
        }
        this.npcTextColor = string.toString();
        string = new StringBuilder();
        for (ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%.");
        this.numberFormat = string.toString();
        string = new StringBuilder();
        for (ChatColor color : colors.get("option")) {
            string.append(color);
        }
        this.optionColor = string.toString();
        string = new StringBuilder();
        for (ChatColor color : colors.get("player")) {
            string.append(color);
        }
        string.append(player.getName() + ChatColor.RESET + ": ");
        for (ChatColor color : colors.get("answer")) {
            string.append(color);
        }
        answerPrefix = string.toString();
        loc = player.getLocation();
        inv = Bukkit.createInventory(null, 9, "NPC");
        player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void setNpcResponse(String npcName, String response) {
        this.npcName = npcName;
        this.response = response.replace('&', '§'); 
    }

    @Override
    public void addPlayerOption(String option) {
        i++;
        options.put(i, option.replace('&', '§'));
    }

    @Override
    public void display() {
        if (response == null) {
            end();
            player.closeInventory();
            return;
        }
        if (options.isEmpty()) {
            end();
        }
        ItemStack[] buttons = new ItemStack[9];
        ItemStack npc = new ItemStack(Material.SKULL_ITEM);
        npc.setDurability((short) 3);
        SkullMeta npcMeta = (SkullMeta) npc.getItemMeta();
        npcMeta.setOwner(npcName);
        npcMeta.setDisplayName(npcNameColor + npcName);
        npcMeta.setLore(stringToLines(response, npcTextColor, null));
        npc.setItemMeta(npcMeta);
        buttons[0] = npc;
        int next = 0;
        for (int j = 2; j < 9; j++) {
            next ++;
            String option = options.get(next);
            if (option == null) {
                break;
            }
            Material material = Material.ENDER_PEARL;
            if (option.matches("^\\{[a-zA-Z_ ]+\\}.*$")) {
                String mName = option.substring(1, option.indexOf('}'));
                Material m = Material.matchMaterial(mName);
                option = option.replace("{" + mName + "}", "");
                if (m == null) {
                    material = Material.ENDER_PEARL;
                } else {
                    material = m;
                }
            }
            buttons[j] = new ItemStack(material);
            ItemMeta meta = buttons[j].getItemMeta();
            meta.setDisplayName(numberFormat.replace("%number%", Integer.toString(next)));
            ArrayList<String> lines = stringToLines(response, npcTextColor, npcNameColor + npcName + ChatColor.RESET + ": ");
            StringBuilder string = new StringBuilder();
            for (ChatColor color : ConversationColors.getColors().get("number")) {
                string.append(color);
            }
            lines.addAll(stringToLines(option, optionColor, string.toString() + "- "));
            meta.setLore(lines);
            buttons[j].setItemMeta(meta);
        }
        player.sendMessage(npcNameColor + npcName + ChatColor.RESET + ": " + npcTextColor + response);
        inv.setContents(buttons);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!((Player) event.getWhoClicked()).equals(player)) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot > 1) {
            int choosen = slot - 1;
            String message = options.get(choosen);
            if (message != null) {
                player.sendMessage(answerPrefix + message);
                inv.setContents(new ItemStack[9]);
                conv.passPlayerAnswer(choosen);
            }
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!((Player) event.getPlayer()).equals(player)) return;
        if (allowClose) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (conv.isMovementBlock()) {
            new BukkitRunnable() {
                public void run() {
                    player.teleport(loc);
                    player.openInventory(inv);
                }
            }.runTask(BetonQuest.getInstance());
        } else {
            conv.endConversation();
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public void clear() {
        response = null;
        options.clear();
        i = 0;
    }

    @Override
    public void end() {
        allowClose = true;
        if (response == null && options.isEmpty()) {
            player.closeInventory();
        }
    }

    private ArrayList<String> stringToLines(String singleLine, String color, String prefix) {
        ArrayList<String> multiLine = new ArrayList<>();
        boolean firstLinePrefix = prefix != null;
        if (prefix == null) prefix = "";
        String[] arr = (prefix + singleLine).split(" ");
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (line.length() + arr[i].length() + 1 > 42) {
                if (firstLinePrefix) {
                    firstLinePrefix = false;
                    multiLine.add(StringUtils.replaceOnce(line.toString().trim(), prefix, prefix + color));
                } else {
                    multiLine.add(color + line.toString().trim());
                }
                line = new StringBuilder();
            }
            line.append(arr[i] + " ");
        }
        if (firstLinePrefix) {
            multiLine.add(StringUtils.replaceOnce(line.toString().trim(), prefix, prefix + color));
        } else {
            multiLine.add(color + line.toString().trim());
        }
        return multiLine;
    }
}
