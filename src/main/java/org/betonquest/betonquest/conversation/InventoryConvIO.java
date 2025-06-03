package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.LocalChatPaginator;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Inventory GUI for conversations.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods", "PMD.CommentRequired", "PMD.CouplingBetweenObjects", "NullAway.Init"})
public class InventoryConvIO implements Listener, ConversationIO {

    private static final Map<String, ItemStack> SKULL_CACHE = new HashMap<>();

    protected final boolean showNumber;

    protected final boolean showNPCText;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * If the messages should also be printed in the chat.
     */
    protected final boolean printMessages;

    @Nullable
    protected Component response;

    protected Map<Integer, Pair<String, Variable<ItemID>>> options = new HashMap<>();

    protected int playerOptionsCount;

    protected Component npcName;

    protected String npcNameColor;

    protected String npcTextColor;

    protected String numberFormat;

    protected String optionColor;

    protected String answerPrefix;

    protected Conversation conv;

    protected OnlineProfile profile;

    protected Inventory inv;

    protected boolean processingLastClick;

    protected boolean allowListenerUnregister;

    protected boolean switching;

    protected Location loc;

    public InventoryConvIO(final Conversation conv, final OnlineProfile onlineProfile, final BetonQuestLogger log,
                           final ConversationColors.Colors colors, final boolean showNumber, final boolean showNPCText, final boolean printMessages) {
        this.log = log;
        this.conv = conv;
        this.profile = onlineProfile;
        this.npcNameColor = collect(colors.npc());
        this.npcTextColor = collect(colors.text());
        this.numberFormat = collect(colors.number()) + "%number%.";
        this.optionColor = collect(colors.option());
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.player()) {
            string.append(color);
        }
        string.append(profile.getPlayer().getName()).append(ChatColor.RESET).append(": ");
        for (final ChatColor color : colors.answer()) {
            string.append(color);
        }
        answerPrefix = string.toString();
        loc = profile.getPlayer().getLocation();

        this.showNumber = showNumber;
        this.showNPCText = showNPCText;
        this.printMessages = printMessages;

        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    private String collect(final ChatColor... chatColors) {
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : chatColors) {
            string.append(color);
        }
        return string.toString();
    }

    @Override
    public void setNpcResponse(final Component npcName, final Component response) {
        this.npcName = npcName;
        this.response = LegacyComponentSerializer.legacySection().deserialize(
                Utils.replaceReset(LegacyComponentSerializer.legacySection().serialize(response), npcTextColor));
    }

    @Override
    public void addPlayerOption(final String option, final ConfigurationSection properties) throws QuestException {
        playerOptionsCount++;
        final String item = properties.getString("item");
        try {
            final Variable<ItemID> variableItem = item == null ? null
                    : new Variable<>(BetonQuest.getInstance().getVariableProcessor(), conv.getPackage(), item,
                    (value) -> new ItemID(conv.getPackage(), value));
            options.put(playerOptionsCount, Pair.of(Utils.replaceReset(option, optionColor), variableItem));
        } catch (final QuestException e) {
            options.put(playerOptionsCount, Pair.of(Utils.replaceReset(option, optionColor), null));
            throw e;
        }
    }

    @SuppressWarnings("PMD.UnusedAssignment")
    @Override
    public void display() {
        if (conv.isEnded()) {
            return;
        }
        if (profile.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            conv.endConversation();
            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> profile.getPlayer().closeInventory());
            final Interceptor interceptor = conv.getInterceptor();
            if (interceptor != null) {
                try {
                    interceptor.sendMessage(BetonQuest.getInstance().getPluginMessage().getMessage(profile, "conversation_spectator"));
                } catch (final QuestException e) {
                    log.warn("Failed to get conversation_spectator message: " + e.getMessage(), e);
                }
            }
            return;
        }

        // each row contains 7 options, so get amount of rows
        int rows = options.size() / 7;
        rows++;
        // this itemstack represents slots in the inventory
        inv = Bukkit.createInventory(null, 9 * rows, Component.text("NPC"));
        inv.setContents(new ItemStack[9 * rows]);
        final ItemStack[] buttons = new ItemStack[9 * rows];
        buttons[0] = createNpcHead();
        generateRows(rows, buttons);

        if (printMessages) {
            Objects.requireNonNull(response);
            conv.sendMessage(npcNameColor + LegacyComponentSerializer.legacySection().serialize(npcName)
                    + ChatColor.RESET + ": " + npcTextColor + LegacyComponentSerializer.legacySection().serialize(response));
        }

        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            inv.setContents(buttons);
            switching = true;
            profile.getPlayer().openInventory(inv);
            switching = false;
            processingLastClick = false;
        });
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private void generateRows(final int rows, final ItemStack... buttons) {
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
            final Pair<String, Variable<ItemID>> pair = options.get(next);
            if (pair == null) {
                break;
            }
            final String option = pair.getKey();
            final Variable<ItemID> itemID = pair.getValue();
            ItemStack item;
            try {
                item = itemID == null ? new ItemStack(Material.ENDER_PEARL)
                        : BetonQuest.getInstance().getFeatureAPI().getItem(itemID.getValue(profile), profile).generate(1);
            } catch (final QuestException e) {
                log.warn("Failed to generate item: " + e.getMessage(), e);
                item = new ItemStack(Material.ENDER_PEARL);
            }
            final ItemMeta meta = item.getItemMeta();

            final StringBuilder string = new StringBuilder();
            for (final ChatColor color : ConversationColors.getColors().number()) {
                string.append(color);
            }

            if (showNumber) {
                meta.setDisplayName(numberFormat.replace("%number%", Integer.toString(next)));
            } else {
                meta.setDisplayName(" ");
            }

            final List<String> lines = new ArrayList<>();

            if (showNPCText) {
                // NPC Text
                Objects.requireNonNull(response);
                lines.addAll(Arrays.asList(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(npcNameColor + LegacyComponentSerializer.legacySection().serialize(npcName)
                                + ChatColor.RESET + ": " + LegacyComponentSerializer.legacySection().serialize(response), npcTextColor),
                        45)));
            }

            // Option Text
            lines.addAll(Arrays.asList(LocalChatPaginator.wordWrap(
                    Utils.replaceReset(string + "- " + option, optionColor),
                    45)));
            meta.setLore(lines);

            item.setItemMeta(meta);
            buttons[j] = item;
        }
    }

    private ItemStack createNpcHead() {
        final String plainTextNpcName = PlainTextComponentSerializer.plainText().serialize(npcName);
        final ItemStack npcHead;
        if (SKULL_CACHE.containsKey(plainTextNpcName)) {
            log.debug(conv.getPackage(), "skull cache hit");
            npcHead = SKULL_CACHE.get(plainTextNpcName);
        } else {
            log.debug(conv.getPackage(), "skull cache miss");
            npcHead = new ItemStack(Material.PLAYER_HEAD);
            npcHead.setDurability((short) 3);
            final SkullMeta npcMeta = (SkullMeta) npcHead.getItemMeta();
            npcMeta.setDisplayName(npcNameColor + LegacyComponentSerializer.legacySection().serialize(npcName));
            npcHead.setItemMeta(npcMeta);
            Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
                try {
                    npcHead.setItemMeta(updateSkullMeta((SkullMeta) npcHead.getItemMeta(), plainTextNpcName));
                    Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                        SKULL_CACHE.put(plainTextNpcName, npcHead);
                        inv.setItem(0, npcHead);
                    });
                } catch (final IllegalArgumentException e) {
                    log.debug(conv.getPackage(), "Could not load skull for chest conversation!", e);
                }
            });
        }

        final SkullMeta npcMeta = (SkullMeta) npcHead.getItemMeta();
        Objects.requireNonNull(response);
        npcMeta.setLore(Arrays.asList(LocalChatPaginator.wordWrap(
                Utils.replaceReset(LegacyComponentSerializer.legacySection().serialize(response), npcTextColor), 45)));
        npcHead.setItemMeta(npcMeta);
        return npcHead;
    }

    @SuppressWarnings("deprecation")
    protected SkullMeta updateSkullMeta(final SkullMeta meta, final String plainTextNpcName) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called async!");
        }
        if (!Bukkit.createProfile(plainTextNpcName).complete()) {
            return meta;
        }
        meta.setOwner(plainTextNpcName);
        return meta;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!event.getWhoClicked().equals(profile.getPlayer())) {
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
            final Pair<String, Variable<ItemID>> pair = options.get(choosen);
            if (pair != null) {
                final String message = pair.getKey();
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
        if (!event.getPlayer().equals(profile.getPlayer())) {
            return;
        }

        // allow for closing previous option inventory
        if (switching) {
            return;
        }
        // allow closing when the conversation has finished
        if (allowListenerUnregister) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (conv.isMovementBlock()) {
            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                profile.getPlayer().teleport(loc);
                profile.getPlayer().openInventory(inv);
            });
        } else {
            conv.endConversation();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent event) {
        final Profile profile = BetonQuest.getInstance().getProfileProvider().getProfile(event.getPlayer());
        if (Conversation.containsPlayer(profile)) {
            event.setCancelled(true);
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
        allowListenerUnregister = true;
        if (mustBeClosed()) {
            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> profile.getPlayer().closeInventory());
        }
    }

    /**
     * Checks whether the inventory must be closed when the conversation ends.
     * <br><br>
     * If a conversation's next option (this was actually it's previous / last option because this is called at the
     * conversation's ending) is null, the previous option was a player's response. If the player ended the
     * conversation we want to close the inventory.
     *
     * @return true if the inventory must be closed
     */
    private boolean mustBeClosed() {
        return inv != null && conv.nextNPCOption == null;
    }
}
