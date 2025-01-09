package org.betonquest.betonquest;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.bukkit.events.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
     * The {@link OnlineProfile} of the player.
     */
    private final OnlineProfile onlineProfile;

    /**
     * Database handler for the player.
     */
    private final PlayerData playerData;

    /**
     * Language of the player.
     */
    private final String lang;

    /**
     * Currently displayed page.
     */
    private Display display;

    /**
     * Creates new backpack GUI opened at given page type.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param type          type of the display
     */
    public Backpack(final OnlineProfile onlineProfile, final DisplayType type) {
        final BetonQuest instance = BetonQuest.getInstance();
        this.log = instance.getLoggerFactory().create(getClass());
        this.onlineProfile = onlineProfile;
        this.playerData = instance.getPlayerDataStorage().get(onlineProfile);
        this.lang = playerData.getLanguage();
        this.display = switch (type) {
            case DEFAULT -> new BackpackPage(1);
            case CANCEL -> new Cancelers();
            case COMPASS -> new Compass();
        };
    }

    /**
     * Creates new backpack GUI.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public Backpack(final OnlineProfile onlineProfile) {
        this(onlineProfile, DisplayType.DEFAULT);
    }

    /**
     * Catches clicks on an open backpack and processes them.
     *
     * @param event the click event
     */
    @EventHandler(ignoreCancelled = true)
    public void onClick(final InventoryClickEvent event) {
        if (event.getWhoClicked().equals(onlineProfile.getPlayer())) {
            // if the player clicked, then cancel this event
            event.setCancelled(true);
            // if the click was outside the inventory, do nothing
            if (event.getRawSlot() < 0) {
                return;
            }
            // pass the click to the Display
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
         * The 'usable' QuestCanceller.
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
        @SuppressWarnings("PMD.NPathComplexity")
        public BackpackPage(final int page) {
            super();
            final boolean showJournalInBackpack = Boolean.parseBoolean(Config.getConfigString("journal.show_in_backpack"));
            this.page = page;
            this.showJournal = showJournalInBackpack && !Journal.hasJournal(onlineProfile);
            this.backpackItems = playerData.getBackpack();
            if (showJournal) {
                backpackItems.add(0, playerData.getJournal().getAsItem());
            }
            this.pages = (int) Math.ceil(backpackItems.size() / 45F);
            this.pageOffset = (page - 1) * SLOT_CANCEL;

            final Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, Config.getMessage(lang, "backpack_title")
                    + (pages == 0 || pages == 1 ? "" : " (" + page + "/" + pages + ")"));
            final ItemStack[] content = new ItemStack[INVENTORY_SIZE];

            for (int index = 0; index < SLOT_CANCEL && pageOffset + index < backpackItems.size(); index++) {
                content[index] = backpackItems.get(pageOffset + index);
            }

            final int pageOne = 1;
            if (page > pageOne) {
                content[SLOT_PREVIOUS] = button("previous", Material.GLOWSTONE_DUST, false).getLeft();
            }
            if (page < pages) {
                content[SLOT_NEXT] = button("next", Material.REDSTONE, false).getLeft();
            }
            final Pair<ItemStack, Boolean> cancel = button("cancel", Material.BONE, true);
            if (cancel.getRight()) {
                showCancel = true;
                content[SLOT_CANCEL] = cancel.getLeft();
            } else {
                showCancel = false;
            }
            final Pair<ItemStack, Boolean> compass = button("compass", Material.COMPASS, true);
            if (compass.getRight()) {
                showCompass = true;
                content[SLOT_COMPASS] = compass.getLeft();
            } else {
                showCompass = false;
            }
            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        private Pair<ItemStack, Boolean> button(final String button, final Material fallback, final boolean checkDefault) {
            ItemStack stack = null;
            boolean present = false;
            final String buttonString = Config.getConfigString("items.backpack." + button + "_button");
            if (buttonString != null && !buttonString.isEmpty()) {
                present = true;
                if (!checkDefault || !"DEFAULT".equalsIgnoreCase(buttonString)) {
                    try {
                        stack = new QuestItem(new ItemID(null, buttonString)).generate(1);
                    } catch (final ObjectNotFoundException | QuestException e) {
                        log.warn("Could not load " + button + " button: " + e.getMessage(), e);
                    }
                }
            }
            if (stack == null) {
                stack = new ItemStack(fallback);
            }
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(Config.getMessage(lang, button).replaceAll("&", "§"));
            stack.setItemMeta(meta);
            return Pair.of(stack, present);
        }

        @SuppressWarnings({"PMD.NcssCount", "PMD.CognitiveComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            if (page == 1 && slot == 0 && showJournal) {
                playerData.getJournal().addToInv();
                display = new BackpackPage(page);
            } else if (slot < SLOT_CANCEL) {
                final int slotId = pageOffset + slot;
                if (backpackItems.size() > slotId) {
                    final ItemStack item = backpackItems.get(slotId);
                    // if the item exists, put it in player's inventory
                    final int backpackAmount = item.getAmount();
                    int getAmount = 0;
                    // left click is one item, right is the whole stack
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
                        // add desired amount of items to player's inventory
                        final ItemStack newItem = item.clone();
                        newItem.setAmount(getAmount);
                        final ItemStack leftItems = onlineProfile.getPlayer().getInventory().addItem(newItem).get(0);
                        // remove from backpack only those items that were
                        // actually added to player's inventory
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
                // slot above 53 is player's inventory, so handle item storing
                final ItemStack item = onlineProfile.getPlayer().getInventory().getItem(playerSlot);
                if (item != null) {
                    final boolean lockJournalSlot = Boolean.parseBoolean(Config.getConfigString("journal.lock_default_journal_slot"));
                    // if the item exists continue
                    if (Utils.isQuestItem(item)) {
                        // if it is a quest item, add it to the backpack
                        int amount = 0;
                        // left click is one item, right is all items
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
                        // add item to backpack and remove it from player's
                        // inventory
                        playerData.addItem(item.clone(), amount);
                        if (item.getAmount() - amount == 0) {
                            onlineProfile.getPlayer().getInventory().setItem(playerSlot, null);
                        } else {
                            item.setAmount(item.getAmount() - amount);
                            onlineProfile.getPlayer().getInventory().setItem(playerSlot, item);
                        }
                    } else if (!lockJournalSlot && Journal.isJournal(onlineProfile, item)) {
                        // if it's a journal, remove it so it appears in
                        // backpack again
                        playerData.getJournal().removeFromInv();
                    }
                    display = new BackpackPage(page);
                }
            } else if (slot == SLOT_PREVIOUS && page > 1) {
                // if it was a previous/next button turn the pages
                display = new BackpackPage(page - 1);
            } else if (slot == SLOT_NEXT && page < pages) {
                display = new BackpackPage(page + 1);
            } else if (slot == SLOT_CANCEL && showCancel) {
                // slot 45 is a slot with quest cancelers
                display = new Cancelers();
            } else if (slot == SLOT_COMPASS && showCompass) {
                // slot 46 is a slot with compass pointers
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
            // get all quest cancelers that can be shown to the player
            for (final QuestCanceler canceler : BetonQuest.getCanceler().values()) {
                if (canceler.show(onlineProfile)) {
                    cancelers.add(canceler);
                }
            }
            // generate the inventory view
            final int size = cancelers.size();
            int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > MAXIMUM_ROWS) {
                numberOfRows = MAXIMUM_ROWS;
                log.warn(onlineProfile + " has too many active quests, please"
                        + " don't allow for so many of them. It slows down your server!");
            }
            final Inventory inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "cancel_page"));
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
            // cancel the chosen quests
            cancel.cancel(onlineProfile);
            onlineProfile.getPlayer().closeInventory();
        }
    }

    /**
     * Showing the possible locations for the compass.
     */
    private class Compass extends Display {
        /**
         * Maps the slot to a location.
         */
        private final Map<Integer, Location> locations = new HashMap<>();

        /**
         * Maps the slot to a QuestCanceler.
         */
        private final Map<Integer, String> names = new HashMap<>();

        /**
         * Maps the slot to an optional Pair of ItemID parts.
         */
        private final Map<Integer, Pair<QuestPackage, String>> items = new HashMap<>();

        /**
         * Creates a page with selectable compass targets and displays it to the player.
         */
        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
        public Compass() {
            super();
            int counter = 0;
            // for every package
            for (final QuestPackage pack : Config.getPackages().values()) {
                final String packName = pack.getQuestPath();
                // loop all compass locations
                final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
                if (section != null) {
                    for (final String key : section.getKeys(false)) {
                        final ConfigurationSection keySection = section.getConfigurationSection(key);
                        final String location = keySection.getString("location");
                        String name;
                        if (keySection.isConfigurationSection("name")) {
                            name = keySection.getString("name." + lang);
                            if (name == null) {
                                name = keySection.getString("name." + Config.getLanguage());
                            }
                            if (name == null) {
                                name = keySection.getString("name.en");
                            }
                        } else {
                            name = keySection.getString("name");
                        }
                        if (name == null) {
                            log.warn("Name not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        if (location == null) {
                            log.warn("Location not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        // check if the player has special compass tag
                        if (!playerData.hasTag(packName + ".compass-" + key)) {
                            continue;
                        }
                        // if the tag is present, continue
                        final Location loc;
                        try {
                            loc = VariableLocation.parse(GlobalVariableResolver.resolve(pack, location));
                        } catch (final QuestException e) {
                            log.warn("Could not parse location in a compass pointer in " + packName
                                    + " package: " + key, e);
                            onlineProfile.getPlayer().closeInventory();
                            return;
                        }
                        // put location with next number
                        locations.put(counter, loc);
                        names.put(counter, GlobalVariableResolver.resolve(pack, name));
                        final String itemName = keySection.getString("item");
                        if (itemName != null) {
                            items.put(counter, Pair.of(pack, GlobalVariableResolver.resolve(pack, itemName)));
                        }
                        counter++;
                    }
                }
            }
            // solve number of needed rows
            final int size = locations.size();
            final int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > MAXIMUM_ROWS) {
                log.warn(onlineProfile + " has too many compass pointers, please"
                        + " don't allow for so many of them. It slows down your server!");
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            final Inventory inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "compass_page"));
            final ItemStack[] content;
            try {
                content = getContent(numberOfRows);
            } catch (final QuestException e) {
                log.warn("Could not load compass button: " + e.getMessage(), e);
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        @SuppressWarnings({"NullAway", "PMD.LocalVariableCouldBeFinal"})
        private ItemStack[] getContent(final int numberOfRows) throws QuestException {
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final Integer slot : locations.keySet()) {
                final Pair<QuestPackage, String> item = items.get(slot);
                if (item == null) {
                    continue;
                }
                ItemStack compass;
                try {
                    compass = new QuestItem(new ItemID(item.getKey(), item.getValue())).generate(1);
                } catch (final ObjectNotFoundException e) {
                    log.warn("Could not find item: " + e.getMessage(), e);
                    compass = new ItemStack(Material.COMPASS);
                }
                final ItemMeta meta = compass.getItemMeta();
                final String name = names.get(slot);
                meta.setDisplayName(name.replace("_", " ").replace("&", "§"));
                compass.setItemMeta(meta);
                content[index] = compass;
                index++;
            }
            return content;
        }

        @Override
        protected void click(final int slot, final int layerSlot, final ClickType click) {
            final Location loc = locations.get(slot);
            if (loc == null) {
                return;
            }
            // set the location of the compass
            final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(onlineProfile, loc);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                onlineProfile.getPlayer().setCompassTarget(loc);
            }
            onlineProfile.getPlayer().closeInventory();
        }
    }
}
