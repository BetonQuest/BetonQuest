package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;

/**
 * Handler for Journals and Quest Items.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CyclomaticComplexity", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public class QuestItemHandler implements Listener {
    /**
     * The config provider.
     */
    private final FileConfigAccessor config;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new quest item handler listener.
     *
     * @param config          the config provider
     * @param dataStorage     the storage providing player data
     * @param profileProvider the profile provider instance
     */
    public QuestItemHandler(final FileConfigAccessor config, final PlayerDataStorage dataStorage,
                            final ProfileProvider profileProvider) {
        this.config = config;
        this.dataStorage = dataStorage;
        this.profileProvider = profileProvider;
    }

    /**
     * Prevents dropping Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the drop item event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        final ItemStack item = event.getItemDrop().getItemStack();
        if (Journal.isJournal(item)) {
            if (isJournalSlotLocked()) {
                event.setCancelled(true);
            } else {
                event.getItemDrop().remove();
            }
        } else if (QuestHandler.isQuestItem(item)) {
            dataStorage.get(onlineProfile).addItem(item.clone(), item.getAmount());
            event.getItemDrop().remove();
        }
    }

    /**
     * Prevents moving the Journal and Quest Items out of the inventory.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the inventory click event, attempting moving items
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onItemMove(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack item = null;
        switch (event.getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
                if (isJournalSlotLocked() && Journal.isJournal(event.getCurrentItem())) {
                    event.setCancelled(true);
                    return;
                }
                break;
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    if (isJournalSlotLocked()) {
                        final ItemStack swapped;
                        if (event.getHotbarButton() == -1 && ClickType.SWAP_OFFHAND == event.getClick()) {
                            swapped = event.getWhoClicked().getInventory().getItemInOffHand();
                        } else {
                            swapped = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                        }
                        if (Journal.isJournal(event.getCurrentItem()) || Journal.isJournal(swapped)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    if (event.getHotbarButton() == -1 && ClickType.SWAP_OFFHAND == event.getClick()) {
                        item = event.getWhoClicked().getInventory().getItemInOffHand();
                    } else {
                        item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                    }
                }
                break;
            case MOVE_TO_OTHER_INVENTORY:
                item = event.getCurrentItem();
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR:
                if (isJournalSlotLocked() && Journal.isJournal(event.getCurrentItem())) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    item = event.getCursor();
                }
                break;
            default:
                break;
        }
        if (Journal.isJournal(item) || QuestHandler.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents moving the Journal and Quest Items out of the inventory.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the inventory drag event, attempting moving items
     */
    @EventHandler(ignoreCancelled = true)
    public void onItemDrag(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (Journal.isJournal(event.getOldCursor()) || QuestHandler.isQuestItem(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents equipping ArmorStands with Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the armor stand interaction event
     */
    @EventHandler(ignoreCancelled = true)
    public void onArmorStandEquip(final PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getPlayerItem();
        if (Journal.isJournal(item) || QuestHandler.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents dropping the Journal and Quest Items on death.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the death event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getEntity());
        // check if there is data for this player; NPCs don't have data
        if (onlineProfile.getOnlineProfile().isEmpty()) {
            return;
        }
        // this prevents the journal from dropping on death by removing it from
        // the list of drops
        final List<ItemStack> drops = event.getDrops();
        final ListIterator<ItemStack> listIterator = drops.listIterator();
        while (listIterator.hasNext()) {
            final ItemStack stack = listIterator.next();
            if (Journal.isJournal(stack)) {
                listIterator.remove();
            } else if (QuestHandler.isQuestItem(stack)) {
                // remove all quest items and add them to backpack
                dataStorage.get(onlineProfile).addItem(stack.clone(), stack.getAmount());
                listIterator.remove();
            }
        }
    }

    /**
     * Moves Quest Items after respawn into the backpack.
     * <p>
     * Does not affect creative mode. Also, gives the Journal back into the inventory.
     *
     * @param event the respawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if (config.getBoolean("item.quest.remove_after_respawn")) {
            // some plugins block item dropping after death and add those
            // items after respawning, so the player doesn't loose his
            // inventory after death; this aims to force removing quest
            // items, as they have been added to the backpack already
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }
            final Inventory inv = event.getPlayer().getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                if (QuestHandler.isQuestItem(inv.getItem(i))) {
                    inv.setItem(i, null);
                }
            }
        }
        if (config.getBoolean("journal.give_on_respawn")) {
            dataStorage.get(profileProvider.getProfile(event.getPlayer())).getJournal().addToInv();
        }
    }

    /**
     * Prevents putting the Journal and Quest Items in Item Frames.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the interact at entity event which is checked for Item Frames
     */
    @EventHandler(ignoreCancelled = true)
    public void onItemFrameClick(final PlayerInteractEntityEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents the journal from being placed inside of item frame
        if (event.getRightClicked() instanceof ItemFrame) {
            final ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
            if (Journal.isJournal(item) || QuestHandler.isQuestItem(item)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents placing Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the block place event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents players from placing "quest item" blocks
        if (QuestHandler.isQuestItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents the breaking of Quest Items.
     *
     * @param event the item break event
     */
    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(final PlayerItemBreakEvent event) {
        if (!config.getBoolean("item.quest.unbreakable")) {
            return;
        }
        if (QuestHandler.isQuestItem(event.getBrokenItem())) {
            final ItemStack original = event.getBrokenItem();
            original.setDurability((short) 0);
            final ItemStack copy = original.clone();
            event.getPlayer().getInventory().removeItem(original);
            event.getPlayer().getInventory().addItem(copy);
        }
    }

    /**
     * Prevents generell in-world interaction with QuestItems and the Journal.
     * <p>
     * Interaction with written books (and so the Journal) is only blocked, when it would interact with a block.
     *
     * @param event the event to process
     */
    @EventHandler
    public void onInteractEvent(final PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE
                || event.useInteractedBlock() == Event.Result.DENY && event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        final ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.getType() == Material.WRITTEN_BOOK) {
            if (QuestHandler.isQuestItem(item) || Journal.isJournal(item)) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        } else if (!EnchantmentTarget.TOOL.includes(item.getType()) && QuestHandler.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents filling bucket Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the bucket fill event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBucketFillEvent(final PlayerBucketFillEvent event) {
        onBucketEvent(event);
    }

    /**
     * Prevents emptying bucket Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the bucket empty event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBucketEmptyEvent(final PlayerBucketEmptyEvent event) {
        onBucketEvent(event);
    }

    /**
     * Prevents interacting with bucket Quest Items.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the bucket event
     */
    public void onBucketEvent(final PlayerBucketEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (QuestHandler.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents moving the Journal and Quest Items out of the inventory with off-hand swapping.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the hand swap event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (isJournalSlotLocked() && (Journal.isJournal(event.getMainHandItem()) || Journal.isJournal(event.getOffHandItem()))) {
            event.setCancelled(true);
        }
    }

    private boolean isJournalSlotLocked() {
        return config.getBoolean("journal.lock_default_journal_slot");
    }
}
