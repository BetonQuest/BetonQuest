package org.betonquest.betonquest.feature;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a chest GUI for the backpack displayed to the player.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class Backpack implements Listener {
    /**
     * The maximum amount of rows an inventory can have.
     */
    private static final int MAXIMUM_ROWS = 6;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The {@link OnlineProfile} of the player.
     */
    private final OnlineProfile onlineProfile;

    /**
     * Database handler for the player.
     */
    private final PlayerData playerData;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor config;

    /**
     * Currently displayed page.
     */
    private Display display;

    /**
     * Creates new backpack GUI opened at given page type.
     *
     * @param config        the plugin configuration file
     * @param pluginMessage the {@link PluginMessage} instance
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param type          type of the display
     */
    public Backpack(final ConfigAccessor config, final PluginMessage pluginMessage, final OnlineProfile onlineProfile, final DisplayType type) {
        this.config = config;
        this.pluginMessage = pluginMessage;
        final BetonQuest instance = BetonQuest.getInstance();
        this.log = instance.getLoggerFactory().create(getClass());
        this.packManager = instance.getQuestPackageManager();
        this.onlineProfile = onlineProfile;
        this.playerData = instance.getPlayerDataStorage().get(onlineProfile);
        this.display = switch (type) {
            case DEFAULT -> new BackpackPage(1);
            case CANCEL -> new Cancelers();
            case COMPASS -> new Compass();
        };
    }

    /**
     * Creates new backpack GUI.
     *
     * @param config        the plugin configuration file
     * @param pluginMessage the {@link PluginMessage} instance
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public Backpack(final ConfigAccessor config, final PluginMessage pluginMessage, final OnlineProfile onlineProfile) {
        this(config, pluginMessage, onlineProfile, DisplayType.DEFAULT);
    }

    /**
     * Catches clicks on an open backpack and processes them.
     *
     * @param event the click event
     */
    @EventHandler(ignoreCancelled = true)
    public void onClick(final InventoryClickEvent event) {
        if (event.getWhoClicked().equals(onlineProfile.getPlayer())) {
            event.setCancelled(true);
            if (event.getRawSlot() < 0) {
                return;
            }
            display.click(event.getRawSlot(), event.getSlot(), event.getClick());
        }
    }

    /**
     * Unregisters the backpack listeners on closing.
     *
     * @param event the close event
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClosing(final InventoryCloseEvent event) {
        if (event.getPlayer().equals(onlineProfile.getPlayer())) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * The parts of the backpack.
     */
    public enum DisplayType {
        /**
         * The QuestItems and buttons.
         */
        DEFAULT,
        /**
         * The 'usable' QuestCanceler.
         */
        CANCEL,
        /**
         * The selectable targets for the Compass.
         */
        COMPASS
    }

    /**
     * Represents a display that can be shown as the backpack.
     */
    private abstract static class Display {
        private Display() {
        }

        /**
         * Processes a click from a {@link InventoryClickEvent}.
         *
         * @param slot       {@link InventoryClickEvent#getRawSlot()}
         * @param playerSlot {@link InventoryClickEvent#getSlot()}
         * @param click      {@link InventoryClickEvent#getClick()}
         */
        protected abstract void click(int slot, int playerSlot, ClickType click);
    }

    /**
     * Standard page with quest items.
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private class BackpackPage extends Display {
        /**
         * Backpack size.
         */
        private static final int INVENTORY_SIZE = 54;

        /**
         * Position of 'cancel' button.
         */
        private static final int SLOT_CANCEL = 45;

        /**
         * Position of 'compass' button.
         */
        private static final int SLOT_COMPASS = 46;

        /**
         * Position of 'next' button.
         */
        private static final int SLOT_NEXT = 50;

        /**
         * Position of 'previous' button.
         */
        private static final int SLOT_PREVIOUS = 48;

        /**
         * The currently shown page.
         */
        private final int page;

        /**
         * The total amount of possible pages.
         */
        private final int pages;

        /**
         * The offset for displaying {@link #backpackItems}.
         */
        private final int pageOffset;

        /**
         * If the journal item should be shown.
         */
        private final boolean showJournal;

        /**
         * If the cancel item should be shown.
         */
        private final boolean showCancel;

        /**
         * If the compass item should be shown.
         */
        private final boolean showCompass;

        /**
         * The (Quest) Items to display.
         */
        private final List<ItemStack> backpackItems;

        /**
         * Creates and displays to the player a given page.
         *
         * @param page number of the page to display, starting from 1
         */
        @SuppressWarnings({"PMD.NPathComplexity", "PMD.CognitiveComplexity"})
        public BackpackPage(final int page) {
            super();
            final boolean showJournalInBackpack = config.getBoolean("journal.show_in_backpack");
            this.page = page;
            this.showJournal = showJournalInBackpack && !Journal.hasJournal(onlineProfile);
            this.backpackItems = playerData.getBackpack();
            if (showJournal) {
                try {
                    backpackItems.add(0, playerData.getJournal().getAsItem());
                } catch (final QuestException e) {
                    log.warn("Could not add journal to backpack: " + e.getMessage(), e);
                }
            }
            this.pages = (int) Math.ceil(backpackItems.size() / 45F);
            this.pageOffset = (page - 1) * SLOT_CANCEL;

            final ItemStack[] content = new ItemStack[INVENTORY_SIZE];

            for (int index = 0; index < SLOT_CANCEL && pageOffset + index < backpackItems.size(); index++) {
                content[index] = backpackItems.get(pageOffset + index);
            }

            final int pageOne = 1;
            if (page > pageOne) {
                content[SLOT_PREVIOUS] = button("previous", Material.GLOWSTONE_DUST).getLeft();
            }
            if (page < pages) {
                content[SLOT_NEXT] = button("next", Material.REDSTONE).getLeft();
            }
            final Pair<ItemStack, Boolean> cancel = button("cancel", Material.BONE);
            if (cancel.getRight()) {
                showCancel = true;
                content[SLOT_CANCEL] = cancel.getLeft();
            } else {
                showCancel = false;
            }
            final Pair<ItemStack, Boolean> compass = button("compass", Material.COMPASS);
            if (compass.getRight()) {
                showCompass = true;
                content[SLOT_COMPASS] = compass.getLeft();
            } else {
                showCompass = false;
            }

            final Inventory inv;
            try {
                Component backpackTitle = pluginMessage.getMessage(onlineProfile, "backpack_title");
                backpackTitle = backpackTitle.append(Component.text(pages == 0 || pages == 1 ? "" : " (" + page + "/" + pages + ")"));
                inv = Bukkit.createInventory(null, INVENTORY_SIZE, backpackTitle);
            } catch (final QuestException e) {
                log.warn("Could not create backpack inventory: " + e.getMessage(), e);
                onlineProfile.getPlayer().closeInventory();
                return;
            }

            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        private Pair<ItemStack, Boolean> button(final String button, final Material fallback) {
            ItemStack stack = null;
            boolean present = false;
            final String buttonString = config.getString("item.backpack." + button + "_button");
            if (buttonString != null && !buttonString.isEmpty()) {
                present = true;
                try {
                    final ItemID itemId = new ItemID(packManager, null, buttonString);
                    stack = BetonQuest.getInstance().getFeatureApi().getItem(itemId, onlineProfile).generate(1);
                } catch (final QuestException e) {
                    log.warn("Could not load " + button + " button: " + e.getMessage(), e);
                }
            }
            if (stack == null) {
                stack = new ItemStack(fallback);
            }
            try {
                final Component name = pluginMessage.getMessage(onlineProfile, button);
                stack.editMeta(meta -> meta.displayName(name));
            } catch (final QuestException e) {
                log.warn("Could not set display name for " + button + " button: " + e.getMessage(), e);
            }
            return Pair.of(stack, present);
        }

        @SuppressWarnings({"PMD.NcssCount", "PMD.CognitiveComplexity", "PMD.NPathComplexity"})
        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            if (page == 1 && slot == 0 && showJournal) {
                playerData.getJournal().addToInv();
                display = new BackpackPage(page);
            } else if (slot < SLOT_CANCEL) {
                final int slotId = pageOffset + slot;
                if (backpackItems.size() > slotId) {
                    final ItemStack item = backpackItems.get(slotId);
                    final int backpackAmount = item.getAmount();
                    int getAmount = 0;
                    switch (click) {
                        case LEFT:
                            getAmount = 1;
                            break;
                        case RIGHT:
                            getAmount = backpackAmount;
                            break;
                        default:
                            break;
                    }
                    if (getAmount != 0) {
                        final ItemStack newItem = item.clone();
                        newItem.setAmount(getAmount);
                        final ItemStack leftItems = onlineProfile.getPlayer().getInventory().addItem(newItem).get(0);
                        int leftAmount = 0;
                        if (leftItems != null) {
                            leftAmount = leftItems.getAmount();
                        }
                        item.setAmount(backpackAmount - getAmount + leftAmount);
                        if (backpackAmount - getAmount + leftAmount == 0) {
                            backpackItems.remove(slotId);
                        }
                        playerData.setBackpack(backpackItems.subList(showJournal ? 1 : 0, backpackItems.size()));
                    }
                    display = new BackpackPage(page);
                }
            } else if (slot >= INVENTORY_SIZE) {
                final ItemStack item = onlineProfile.getPlayer().getInventory().getItem(playerSlot);
                if (item != null) {
                    final boolean lockJournalSlot = config.getBoolean("journal.lock_default_journal_slot");
                    // if the item exists continue
                    if (Utils.isQuestItem(item)) {
                        int amount = 0;
                        switch (click) {
                            case LEFT:
                                amount = 1;
                                break;
                            case RIGHT:
                                amount = item.getAmount();
                                break;
                            default:
                                break;
                        }
                        playerData.addItem(item.clone(), amount);
                        if (item.getAmount() - amount == 0) {
                            onlineProfile.getPlayer().getInventory().setItem(playerSlot, null);
                        } else {
                            item.setAmount(item.getAmount() - amount);
                            onlineProfile.getPlayer().getInventory().setItem(playerSlot, item);
                        }
                    } else if (!lockJournalSlot && Journal.isJournal(onlineProfile, item)) {
                        playerData.getJournal().removeFromInv();
                    }
                    display = new BackpackPage(page);
                }
            } else if (slot == SLOT_PREVIOUS && page > 1) {
                display = new BackpackPage(page - 1);
            } else if (slot == SLOT_NEXT && page < pages) {
                display = new BackpackPage(page + 1);
            } else if (slot == SLOT_CANCEL && showCancel) {
                display = new Cancelers();
            } else if (slot == SLOT_COMPASS && showCompass) {
                display = new Compass();
            }
        }
    }

    /**
     * The page with quest cancelers.
     */
    private class Cancelers extends Display {

        /**
         * Maps the slot to a QuestCanceler.
         */
        private final Map<Integer, QuestCanceler> map = new HashMap<>();

        /**
         * Creates a page with quest cancelers and displays it to the player.
         */
        public Cancelers() {
            super();
            final List<QuestCanceler> cancelers = new ArrayList<>();
            for (final Map.Entry<QuestCancelerID, QuestCanceler> entry : BetonQuest.getInstance().getFeatureApi().getCancelers().entrySet()) {
                try {
                    if (entry.getValue().isCancelable(onlineProfile)) {
                        cancelers.add(entry.getValue());
                    }
                } catch (final QuestException e) {
                    log.warn(entry.getKey().getPackage(), "Could not check if canceler is cancelable, don't show it in the GUI: " + e.getMessage(), e);
                }
            }
            final int size = cancelers.size();
            int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > MAXIMUM_ROWS) {
                numberOfRows = MAXIMUM_ROWS;
                log.warn(onlineProfile + " has too many active quests, please"
                        + " don't allow for so many of them. It slows down your server!");
            }
            final Inventory inv;
            try {
                inv = Bukkit.createInventory(null, numberOfRows * 9, pluginMessage.getMessage(onlineProfile, "cancel_page"));
            } catch (final QuestException e) {
                log.warn("Could not create cancel inventory: " + e.getMessage(), e);
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final QuestCanceler canceler : cancelers) {
                content[index] = canceler.getItem(onlineProfile);
                map.put(index, canceler);
                index++;
            }
            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            final QuestCanceler cancel = map.get(slot);
            if (cancel == null) {
                return;
            }
            cancel.cancel(onlineProfile, false);
            onlineProfile.getPlayer().closeInventory();
        }
    }

    /**
     * Showing the possible locations for the compass.
     */
    private class Compass extends Display {
        /**
         * Maps the slot to a compass.
         */
        private final Map<Integer, QuestCompass> compasses = new HashMap<>();

        /**
         * Creates a page with selectable compass targets and displays it to the player.
         */
        public Compass() {
            super();
            int counter = 0;
            final FeatureApi featureApi = BetonQuest.getInstance().getFeatureApi();
            for (final Map.Entry<CompassID, QuestCompass> entry : featureApi.getCompasses().entrySet()) {
                if (playerData.hasTag(entry.getKey().getTag())) {
                    compasses.put(counter, entry.getValue());
                    counter++;
                }
            }

            final int size = compasses.size();
            final int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > MAXIMUM_ROWS) {
                log.warn(onlineProfile + " has too many compass pointers, please"
                        + " don't allow for so many of them. It slows down your server!");
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            final Inventory inv;
            try {
                inv = Bukkit.createInventory(null, numberOfRows * 9, pluginMessage.getMessage(onlineProfile, "compass_page"));
            } catch (final QuestException e) {
                log.warn("Could not create compass inventory: " + e.getMessage(), e);
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            inv.setContents(getContent(numberOfRows));
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        @SuppressWarnings("NullAway")
        private ItemStack[] getContent(final int numberOfRows) {
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final Map.Entry<Integer, QuestCompass> entry : compasses.entrySet()) {
                final QuestCompass comp = entry.getValue();
                final ItemID item = comp.itemID();
                if (item == null) {
                    continue;
                }
                ItemStack compass;
                try {
                    compass = BetonQuest.getInstance().getFeatureApi().getItem(item, onlineProfile).generate(1);
                } catch (final QuestException e) {
                    log.warn("Could not find item: " + e.getMessage(), e);
                    compass = new ItemStack(Material.COMPASS);
                }
                final Component name;
                try {
                    name = comp.names().asComponent(onlineProfile);
                } catch (final QuestException e) {
                    log.warn("Could not get name for compass '" + entry.getKey() + "', skipping it: " + e.getMessage(), e);
                    continue;
                }
                final ItemMeta meta = compass.getItemMeta();
                meta.displayName(name);
                compass.setItemMeta(meta);
                content[index] = compass;
                index++;
            }
            return content;
        }

        @Override
        protected void click(final int slot, final int layerSlot, final ClickType click) {
            final QuestCompass compass = compasses.get(slot);
            if (compass == null) {
                return;
            }
            final Location loc;
            try {
                loc = compass.location().getValue(onlineProfile);
            } catch (final QuestException e) {
                log.warn("Could not resolve compass location for '" + compass + "': " + e.getMessage(), e);
                return;
            }

            if (new QuestCompassTargetChangeEvent(onlineProfile, loc).callEvent()) {
                onlineProfile.getPlayer().setCompassTarget(loc);
            }
            onlineProfile.getPlayer().closeInventory();
        }
    }
}
