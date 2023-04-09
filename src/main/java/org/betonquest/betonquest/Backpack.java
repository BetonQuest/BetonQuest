package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition"})
public class Backpack implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(Backpack.class);

    /**
     * The {@link OnlineProfile} of the player
     */
    private final OnlineProfile onlineProfile;
    /**
     * Database handler for the player
     */
    private final PlayerData playerData;
    /**
     * Language of the player
     */
    private final String lang;
    /**
     * The inventory created by this object
     */
    private Inventory inv;
    /**
     * Currently displayed page
     */
    private Display display;

    /**
     * Creates new backpack GUI opened at given page type.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param type          type of the display
     */
    public Backpack(final OnlineProfile onlineProfile, final DisplayType type) {
        // fill required fields
        this.onlineProfile = onlineProfile;
        lang = BetonQuest.getInstance().getPlayerData(onlineProfile).getLanguage();
        /**
         * Instance of the BetonQuest plugin
         */
        final BetonQuest instance = BetonQuest.getInstance();
        playerData = instance.getPlayerData(onlineProfile);
        // create display
        switch (type) {
            case DEFAULT:
                display = new Page(1);
                break;
            case CANCEL:
                display = new Cancelers();
                break;
            case COMPASS:
                display = new Compass();
                break;
        }
    }

    /**
     * Creates new backpack GUI.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public Backpack(final OnlineProfile onlineProfile) {
        this(onlineProfile, DisplayType.DEFAULT);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(final InventoryClickEvent event) {
        if (event.getWhoClicked().equals(onlineProfile.getPlayer())) {
            // if the player clicked, then cancel this event
            event.setCancelled(true);
            // if the click was outside of the inventory, do nothing
            if (event.getRawSlot() < 0) {
                return;
            }
            // pass the click to the Display
            display.click(event.getRawSlot(), event.getSlot(), event.getClick());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClosing(final InventoryCloseEvent event) {
        if (event.getPlayer().equals(onlineProfile.getPlayer())) {
            HandlerList.unregisterAll(this);
        }
    }

    public enum DisplayType {
        DEFAULT, CANCEL, COMPASS
    }

    /**
     * Represents a display that can be shown as the backpack.
     */
    private abstract class Display {
        private Display() {
        }

        protected abstract void click(int slot, int playerSlot, ClickType click);
    }

    /**
     * Standard page with quest items.
     */
    @SuppressWarnings({"PMD.ShortClassName", "PMD.CyclomaticComplexity", "PMD.AvoidFieldNameMatchingTypeName"})
    private class Page extends Display {
        private final int page;
        private final int pages;
        private final int pageOffset;
        private final boolean showJournal;
        private final boolean showCancel;
        private final boolean showCompass;
        private final List<ItemStack> backpackItems;

        /**
         * Creates and displays to the player a given page.
         *
         * @param page number of the page to display, starting from 1
         */
        @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        public Page(final int page) {
            super();
            final boolean showJournalInBackpack = Boolean.parseBoolean(Config.getString("config.journal.show_in_backpack"));
            this.page = page;
            this.showJournal = showJournalInBackpack && !Journal.hasJournal(onlineProfile);
            this.backpackItems = playerData.getBackpack();
            if (showJournal) {
                backpackItems.add(0, playerData.getJournal().getAsItem());
            }
            this.pages = (int) Math.ceil(backpackItems.size() / 45F);
            this.pageOffset = (page - 1) * 45;

            inv = Bukkit.createInventory(null, 54, Config.getMessage(lang, "backpack_title")
                    + (pages == 0 || pages == 1 ? "" : " (" + page + "/" + pages + ")"));
            final ItemStack[] content = new ItemStack[54];

            for (int index = 0; index < 45 && pageOffset + index < backpackItems.size(); index++) {
                content[index] = backpackItems.get(pageOffset + index);
            }

            // if there are other pages, place the buttons
            if (page > 1) {
                ItemStack previous = null;
                final String previousButton = Config.getString("config.items.backpack.previous_button");
                if (previousButton != null && !previousButton.isEmpty()) {
                    try {
                        previous = new QuestItem(new ItemID(null, previousButton)).generate(1);
                    } catch (final ObjectNotFoundException | InstructionParseException e) {
                        LOG.warn("Could not load previous button: " + e.getMessage(), e);
                    }
                }
                if (previous == null) {
                    previous = new ItemStack(Material.GLOWSTONE_DUST);
                }
                final ItemMeta meta = previous.getItemMeta();
                meta.setDisplayName(Config.getMessage(lang, "previous").replaceAll("&", "§"));
                previous.setItemMeta(meta);
                content[48] = previous;
            }
            if (page < pages) {
                ItemStack next = null;
                final String nextButton = Config.getString("config.items.backpack.next_button");
                if (nextButton != null && !nextButton.isEmpty()) {
                    try {
                        next = new QuestItem(new ItemID(null, nextButton)).generate(1);
                    } catch (final ObjectNotFoundException | InstructionParseException e) {
                        LOG.warn("Could not load next button: " + e.getMessage(), e);
                    }
                }
                if (next == null) {
                    next = new ItemStack(Material.REDSTONE);
                }
                final ItemMeta meta = next.getItemMeta();
                meta.setDisplayName(Config.getMessage(lang, "next").replaceAll("&", "§"));
                next.setItemMeta(meta);
                content[50] = next;
            }
            // set "cancel quest" button
            ItemStack cancel = null;
            final String cancelButton = Config.getString("config.items.backpack.cancel_button");
            if (cancelButton != null && !cancelButton.isEmpty()) {
                showCancel = true;
                if (!"DEFAULT".equalsIgnoreCase(cancelButton)) {
                    try {
                        cancel = new QuestItem(new ItemID(null, cancelButton)).generate(1);
                    } catch (final ObjectNotFoundException | InstructionParseException e) {
                        LOG.warn("Could not load cancel button: " + e.getMessage(), e);
                    }
                }
                if (cancel == null) {
                    cancel = new ItemStack(Material.BONE);
                }
                final ItemMeta meta = cancel.getItemMeta();
                meta.setDisplayName(Config.getMessage(lang, "cancel").replaceAll("&", "§"));
                cancel.setItemMeta(meta);
                content[45] = cancel;
                // set "compass targets" button
            } else {
                showCancel = false;
            }
            ItemStack compassItem = null;
            final String compassButton = Config.getString("config.items.backpack.compass_button");
            if (compassButton != null && !compassButton.isEmpty()) {
                showCompass = true;
                if (!"DEFAULT".equalsIgnoreCase(compassButton)) {
                    try {
                        compassItem = new QuestItem(new ItemID(null, compassButton)).generate(1);
                    } catch (final ObjectNotFoundException | InstructionParseException e) {
                        LOG.warn("Could not load compass button: " + e.getMessage(), e);
                    }
                }
                if (compassItem == null) {
                    compassItem = new ItemStack(Material.COMPASS);
                }
                final ItemMeta compassMeta = compassItem.getItemMeta();
                compassMeta.setDisplayName(Config.getMessage(lang, "compass").replace('&', '&'));
                compassItem.setItemMeta(compassMeta);
                content[46] = compassItem;
                // set the inventory and display it
            } else {
                showCompass = false;
            }
            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
        }

        @SuppressWarnings({"PMD.NcssCount", "PMD.CognitiveComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            if (page == 1 && slot == 0 && showJournal) {
                playerData.getJournal().addToInv();
                display = new Page(page);
            } else if (slot < 45) {
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
                    display = new Page(page);
                }
            } else if (slot > 53) {
                // slot above 53 is player's inventory, so handle item storing
                final ItemStack item = onlineProfile.getPlayer().getInventory().getItem(playerSlot);
                if (item != null) {
                    final boolean lockJournalSlot = Boolean.parseBoolean(Config.getString("config.journal.lock_default_journal_slot"));
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
                    display = new Page(page);
                }
            } else if (slot == 48 && page > 1) {
                // if it was a previous/next button turn the pages
                display = new Page(page - 1);
            } else if (slot == 50 && page < pages) {
                display = new Page(page + 1);
            } else if (slot == 45 && showCancel) {
                // slot 45 is a slot with quest cancelers
                display = new Cancelers();
            } else if (slot == 46 && showCompass) {
                // slot 46 is a slot with compass pointers
                display = new Compass();
            }
        }

    }

    /**
     * The page with quest cancelers.
     */
    private class Cancelers extends Display {

        private final Map<Integer, QuestCanceler> map = new HashMap<>();

        /**
         * Creates a page with quest cancelers and displays it to the player.
         */
        public Cancelers() {
            super();
            final ArrayList<QuestCanceler> cancelers = new ArrayList<>();
            // get all quest cancelers that can be shown to the player
            for (final QuestCanceler canceler : BetonQuest.getCanceler().values()) {
                if (canceler.show(onlineProfile)) {
                    cancelers.add(canceler);
                }
            }
            // generate the inventory view
            final int size = cancelers.size();
            int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > 6) {
                numberOfRows = 6;
                LOG.warn(onlineProfile + " has too many active quests, please"
                        + " don't allow for so many of them. It slows down your server!");
            }
            inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "cancel_page"));
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

    private class Compass extends Display {

        private final Map<Integer, Location> locations = new HashMap<>();
        private final Map<Integer, String> names = new HashMap<>();
        private final Map<Integer, Pair<QuestPackage, String>> items = new HashMap<>();

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.NPathComplexity",
                "PMD.CognitiveComplexity"})
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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
                        final String location = pack.getString("compass." + key + ".location");
                        String name;
                        if (section.isConfigurationSection(key + ".name")) {
                            name = pack.getString("compass." + key + ".name." + lang);
                            if (name == null) {
                                name = pack.getString("compass." + key + ".name." + Config.getLanguage());
                            }
                            if (name == null) {
                                name = pack.getString("compass." + key + ".name.en");
                            }
                        } else {
                            name = pack.getString("compass." + key + ".name");
                        }
                        if (name == null) {
                            LOG.warn("Name not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        if (location == null) {
                            LOG.warn("Location not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        // check if the player has special compass tag
                        if (!playerData.hasTag(packName + ".compass-" + key)) {
                            continue;
                        }
                        // if the tag is present, continue
                        final String[] parts = location.split(";");
                        if (parts.length != 4) {
                            LOG.warn("Could not parse location in a compass pointer in " + packName + " package: "
                                    + key);
                            continue;
                        }
                        final World world = Bukkit.getWorld(parts[3]);
                        if (world == null) {
                            LOG.warn("World does not exist in a compass pointer in " + packName + " package: " + key);
                        }
                        final int locX;
                        final int locY;
                        final int locZ;
                        try {
                            locX = Integer.parseInt(parts[0]);
                            locY = Integer.parseInt(parts[1]);
                            locZ = Integer.parseInt(parts[2]);
                        } catch (final NumberFormatException e) {
                            LOG.warn("Could not parse location coordinates in a compass pointer in " + packName
                                    + " package: " + key, e);
                            onlineProfile.getPlayer().closeInventory();
                            return;
                        }
                        final Location loc = new Location(world, locX, locY, locZ);
                        // put location with next number
                        locations.put(counter, loc);
                        names.put(counter, name);
                        final String itemName = pack.getString("compass." + key + ".item");
                        if (itemName != null) {
                            items.put(counter, Pair.of(pack, itemName));
                        }
                        counter++;
                    }
                }
            }
            // solve number of needed rows
            final int size = locations.size();
            final int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > 6) {
                LOG.warn(onlineProfile + " has too many compass pointers, please"
                        + " don't allow for so many of them. It slows down your server!");
                onlineProfile.getPlayer().closeInventory();
                return;
            }
            inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "compass_page"));
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final Integer slot : locations.keySet()) {
                final Pair<QuestPackage, String> item = items.get(slot);
                ItemStack compass;
                try {
                    compass = new QuestItem(new ItemID(item.getKey(), item.getValue())).generate(1);
                } catch (final InstructionParseException e) {
                    LOG.warn("Could not load compass button: " + e.getMessage(), e);
                    onlineProfile.getPlayer().closeInventory();
                    return;
                } catch (final ObjectNotFoundException e) {
                    LOG.warn("Could not find item: " + e.getMessage(), e);
                    compass = new ItemStack(Material.COMPASS);
                }
                final ItemMeta meta = compass.getItemMeta();
                final String name = names.get(slot);
                meta.setDisplayName(name.replace("_", " ").replace("&", "§"));
                compass.setItemMeta(meta);
                content[index] = compass;
                index++;
            }
            inv.setContents(content);
            onlineProfile.getPlayer().openInventory(inv);
            Bukkit.getPluginManager().registerEvents(Backpack.this, BetonQuest.getInstance());
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
