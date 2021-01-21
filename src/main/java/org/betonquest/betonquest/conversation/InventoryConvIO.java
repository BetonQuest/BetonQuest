package org.betonquest.betonquest.conversation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.LocalChatPaginator;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
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

import java.util.*;
import java.util.logging.Level;

/**
 * Inventory GUI for conversations
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.CommentRequired", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.AvoidLiteralsInIfCondition"})
public class InventoryConvIO implements Listener, ConversationIO {
    private static final Map<String, ItemStack> SKULL_CACHE = new HashMap<>();

    protected String response;
    protected Map<Integer, String> options = new HashMap<>();
    protected int playerOptionsCount;
    protected String npcName;
    protected String npcNameColor;
    protected String npcTextColor;
    protected String numberFormat;
    protected String optionColor;
    protected String answerPrefix;
    protected Conversation conv;
    protected Player player;
    protected Inventory inv;
    protected boolean processingLastClick;
    protected boolean allowClose;
    protected boolean switching;
    protected Location loc;
    protected boolean printMessages;

    // Config
    protected boolean showNumber = true;
    protected boolean showNPCText = true;

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public InventoryConvIO(final Conversation conv, final String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
        final Map<String, ChatColor[]> colors = ConversationColors.getColors();
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
        string.append(player.getName()).append(ChatColor.RESET).append(": ");
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

    @SuppressWarnings({"deprecation", "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "PMD.NcssCount",
            "PMD.NPathComplexity", "PMD.AvoidUsingShortType"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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
        int rows = options.size() / 7;
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
                } catch (final IllegalArgumentException e) {
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
                    } catch (final NumberFormatException e) {
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
            final int row = slot / 9;
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
                @Override
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

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected List<String> stringToLines(final String singleLine, final String color, final String prefix) {
        String inputPrefix = prefix;
        final ArrayList<String> multiLine = new ArrayList<>();
        boolean firstLinePrefix = inputPrefix != null;
        if (inputPrefix == null) {
            inputPrefix = "";
        }
        final String[] lineBreaks = (inputPrefix + singleLine).split("\n");
        for (final String brokenLine : lineBreaks) {
            final String[] arr = brokenLine.split(" ");
            StringBuilder line = new StringBuilder();
            for (final String s : arr) {
                //don't count color codes for line length
                final int rawLength = ChatColor.stripColor(line.toString()).length() + ChatColor.stripColor(s).length();
                if (rawLength + 1 > 42) {
                    if (firstLinePrefix) {
                        firstLinePrefix = false;
                        multiLine.add(StringUtils.replaceOnce(line.toString().trim(), inputPrefix, inputPrefix + color));
                    } else {
                        multiLine.add(color + line.toString().trim());
                    }
                    line = new StringBuilder();
                }
                line.append(s).append(" ");
            }
            if (firstLinePrefix) {
                firstLinePrefix = false;
                multiLine.add(StringUtils.replaceOnce(line.toString().trim(), inputPrefix, inputPrefix + color));
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
