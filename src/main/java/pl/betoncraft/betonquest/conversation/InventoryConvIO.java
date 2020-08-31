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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import pl.betoncraft.betonquest.utils.LocalChatPaginator;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Inventory GUI for conversations
 *
 * @author Jakub Sapalski
 */
public class InventoryConvIO implements Listener, ConversationIO {
    private static final HashMap<String, ItemStack> SKULL_CACHE = new HashMap<>();

    protected String response = null;
    protected HashMap<Integer, String> options = new HashMap<>();
    protected int playerOptionsCount = 0;
    protected String npcName;
    protected String npcNameColor;
    protected String npcTextColor;
    protected String numberFormat;
    protected String optionColor;
    protected String answerPrefix;
    protected Conversation conv;
    protected Player player;
    protected Inventory inv;
    protected boolean processingLastClick = false;
    protected boolean allowClose = false;
    protected boolean switching = false;
    protected Location loc;
    protected boolean printMessages = false;

    // Config
    protected boolean showNumber = true;
    protected boolean showNPCText = true;

    public InventoryConvIO(final Conversation conv, final String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
        final HashMap<String, ChatColor[]> colors = ConversationColors.getColors();
        StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.get("npc")) {
            string.append(color);
        }
        this.npcNameColor = string.toString();
        string = new StringBuilder();
        for (final ChatColor color : colors.get("text")) {
            string.append(color);
        }
        this.npcTextColor = string.toString();
        string = new StringBuilder();
        for (final ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%.");
        this.numberFormat = string.toString();
        string = new StringBuilder();
        for (final ChatColor color : colors.get("option")) {
            string.append(color);
        }
        this.optionColor = string.toString();
        string = new StringBuilder();
        for (final ChatColor color : colors.get("player")) {
            string.append(color);
        }
        string.append(player.getName() + ChatColor.RESET + ": ");
        for (final ChatColor color : colors.get("answer")) {
            string.append(color);
        }
        answerPrefix = string.toString();
        loc = player.getLocation();

        // Load config
        if (BetonQuest.getInstance().getConfig().contains("conversation_IO_config.chest")) {
            final ConfigurationSection config = BetonQuest.getInstance().getConfig().getConfigurationSection("conversation_IO_config.chest");
            showNumber = config.getBoolean("show_number", true);
            showNPCText = config.getBoolean("show_npc_text", true);
        }

        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void setNpcResponse(final String npcName, final String response) {
        this.npcName = npcName;
        this.response = Utils.replaceReset(response, npcTextColor);
    }

    @Override
    public void addPlayerOption(final String option) {
        playerOptionsCount++;
        options.put(playerOptionsCount, Utils.replaceReset(option, optionColor));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void display() {
        // prevent displaying anything if the player closed the conversation
        // in the meantime
        if (conv.isEnded()) {
            return;
        }
        if (response == null) {
            end();
            player.closeInventory();
            return;
        }
        if (options.isEmpty()) {
            end();
        }
        // each row contains 7 options, so get amount of rows
        int rows = (int) Math.floor(options.size() / 7);
        rows++;
        // this itemstack represents slots in the inventory
        inv = Bukkit.createInventory(null, 9 * rows, "NPC");
        inv.setContents(new ItemStack[9 * rows]);
        final ItemStack[] buttons = new ItemStack[9 * rows];
        // set the NPC head
        final ItemStack npc;
        if (SKULL_CACHE.containsKey(npcName) && false) {
            npc = SKULL_CACHE.get(npcName);
        } else {
            npc = new ItemStack(Material.PLAYER_HEAD);
            npc.setDurability((short) 3);
            final SkullMeta npcMeta = (SkullMeta) npc.getItemMeta();
            npcMeta.setDisplayName(npcNameColor + npcName);
            // NPC Text
            npcMeta.setLore(Arrays.asList(LocalChatPaginator.wordWrap(
                    Utils.replaceReset(response, npcTextColor),
                    45)));

            npc.setItemMeta(npcMeta);
            Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
                try {
                    npc.setItemMeta(updateSkullMeta((SkullMeta) npc.getItemMeta()));
                    Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                        SKULL_CACHE.put(npcName, npc);
                        inv.setItem(0, npc);
                    });
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.FINE, "Could not load skull for chest conversation!", e);
                }
            });
        }
        buttons[0] = npc;
        // this is the number of an option
        int next = 0;
        // now fill the slots
        for (int j = 0; j < 9 * rows; j++) {
            // skip first and second slots of each row
            if (j % 9 == 0 || j % 9 == 1) {
                continue;
            }
            // count option numbers, starting with 1
            next++;
            // break if all options are set
            String option = options.get(next);
            if (option == null) {
                break;
            }
            // generate an itemstack for this option
            Material material = Material.ENDER_PEARL;
            short data = 0;
            // get the custom material
            if (option.matches("^\\{[a-zA-Z0-9_: ]+\\}(?s:.*)$")) {
                final String fullMaterial = option.substring(1, option.indexOf('}'));
                String materialName = fullMaterial;
                if (materialName.contains(":")) {
                    final int colonIndex = materialName.indexOf(':');
                    try {
                        data = Short.valueOf(materialName.substring(colonIndex + 1));
                    } catch (NumberFormatException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Could not read material data: " + e.getMessage());
                        LogUtils.logThrowable(e);
                        data = 0;
                    }
                    materialName = materialName.substring(0, colonIndex);
                }
                Material mat = Material.matchMaterial(materialName);
                if (mat == null) {
                    mat = Material.matchMaterial(materialName, true);
                }
                option = option.replace("{" + fullMaterial + "}", "");
                if (mat == null) {
                    material = Material.ENDER_PEARL;
                } else {
                    material = mat;
                }
            }
            // remove custom material prefix from the option
            options.put(next, option);
            // set the display name and lore of the option
            final ItemStack item = new ItemStack(material);
            item.setDurability(data);
            final ItemMeta meta = item.getItemMeta();

            final StringBuilder string = new StringBuilder();
            for (final ChatColor color : ConversationColors.getColors().get("number")) {
                string.append(color);
            }

            if (showNumber) {
                meta.setDisplayName(numberFormat.replace("%number%", Integer.toString(next)));
            } else {
                meta.setDisplayName(" ");
            }

            final ArrayList<String> lines = new ArrayList<>();

            if (showNPCText) {
                // NPC Text
                lines.addAll(Arrays.asList(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(npcNameColor + npcName + ChatColor.RESET + ": " +
                                response, npcTextColor),
                        45)));
            }

            // Option Text
            lines.addAll(Arrays.asList(LocalChatPaginator.wordWrap(
                    Utils.replaceReset(string.toString() + "- " + option, optionColor),
                    45)));
            meta.setLore(lines);

            item.setItemMeta(meta);
            buttons[j] = item;
        }
        if (printMessages) {
            conv.sendMessage(npcNameColor + npcName + ChatColor.RESET + ": " + npcTextColor + response);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                inv.setContents(buttons);
                switching = true;
                player.openInventory(inv);
                switching = false;
                processingLastClick = false;
            }
        }.runTask(BetonQuest.getInstance());
    }

    @SuppressWarnings("deprecation")
    protected SkullMeta updateSkullMeta(final SkullMeta meta) {
        meta.setOwner(npcName);
        return meta;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!event.getWhoClicked().equals(player)) {
            return;
        }
        event.setCancelled(true);
        if (processingLastClick) {
            return;
        }
        final int slot = event.getRawSlot();
        // calculate the option number
        if (slot % 9 > 1) {
            final int row = (int) Math.floor(slot / 9);
            // raw column number minus two columns (npc head an empty space)
            // and plus one (because options are indexed starting with 1)
            final int col = slot % 9 - 2 + 1;
            // each row can have 7 options, add column number to get an option
            final int choosen = row * 7 + col;
            final String message = options.get(choosen);
            if (message != null) {
                processingLastClick = true;
                if (printMessages) {
                    conv.sendMessage(answerPrefix + message);
                }
                conv.passPlayerAnswer(choosen);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!event.getPlayer().equals(player)) {
            return;
        }

        // allow for closing previous option inventory
        if (switching) {
            return;
        }
        // allow closing when the conversation has finished
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
        playerOptionsCount = 0;
    }

    @Override
    public void end() {
        allowClose = true;
        if (response == null && options.isEmpty() && inv != null) {
            player.closeInventory();
        }
    }

    @Override
    public boolean printMessages() {
        return printMessages;
    }

    protected ArrayList<String> stringToLines(final String singleLine, final String color, String prefix) {
        final ArrayList<String> multiLine = new ArrayList<>();
        boolean firstLinePrefix = prefix != null;
        if (prefix == null) {
            prefix = "";
        }
        final String[] lineBreaks = (prefix + singleLine).split("\n");
        for (final String brokenLine : lineBreaks) {
            final String[] arr = brokenLine.split(" ");
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                //don't count color codes for line length
                final int rawLength = ChatColor.stripColor(line.toString()).length() + ChatColor.stripColor(arr[i]).length();
                if (rawLength + 1 > 42) {
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
                firstLinePrefix = false;
                multiLine.add(StringUtils.replaceOnce(line.toString().trim(), prefix, prefix + color));
            } else {
                multiLine.add(color + line.toString().trim());
            }
        }
        return multiLine;
    }

    /**
     * Inventory GUI that also outputs the conversation to chat
     */
    public static class Combined extends InventoryConvIO {

        public Combined(final Conversation conv, final String playerID) {
            super(conv, playerID);
            super.printMessages = true;
        }
    }
}
