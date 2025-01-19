package org.betonquest.betonquest.item;

import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.util.Utils;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;

/**
 * Handler for Journals.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.CommentRequired", "PMD.CyclomaticComplexity"})
public class QuestItemHandler implements Listener {
    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates a new quest item handler listener.
     *
     * @param dataStorage the storage providing player data
     */
    public QuestItemHandler(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        final ItemStack item = event.getItemDrop().getItemStack();
        if (Journal.isJournal(onlineProfile, item)) {
            if (isJournalSlotLocked()) {
                event.setCancelled(true);
            } else {
                event.getItemDrop().remove();
            }
        } else if (Utils.isQuestItem(item)) {
            dataStorage.get(onlineProfile).addItem(item.clone(), item.getAmount());
            event.getItemDrop().remove();
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onItemMove(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getWhoClicked());
        ItemStack item = null;
        switch (event.getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
                if (isJournalSlotLocked() && Journal.isJournal(onlineProfile, event.getCurrentItem())) {
                    event.setCancelled(true);
                    return;
                }
                break;
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    if (isJournalSlotLocked()) {
                        final ItemStack swapped;
                        if (event.getHotbarButton() == -1 && "SWAP_OFFHAND".equals(event.getClick().name())) {
                            swapped = event.getWhoClicked().getInventory().getItemInOffHand();
                        } else {
                            swapped = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                        }
                        if (Journal.isJournal(onlineProfile, event.getCurrentItem()) || Journal.isJournal(onlineProfile, swapped)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    if (event.getHotbarButton() == -1 && "SWAP_OFFHAND".equals(event.getClick().name())) {
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
                if (isJournalSlotLocked() && Journal.isJournal(onlineProfile, event.getCurrentItem())) {
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
        if (Journal.isJournal(onlineProfile, item) || Utils.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrag(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getWhoClicked());
        if (Journal.isJournal(onlineProfile, event.getOldCursor()) || Utils.isQuestItem(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandEquip(final PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getPlayerItem();
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (Journal.isJournal(onlineProfile, item) || Utils.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getEntity());
        // check if there is data for this player; NPCs don't have data
        if (onlineProfile.getOnlineProfile().isEmpty()) {
            return;
        }
        // this prevents the journal from dropping on death by removing it from
        // the list of drops
        final List<ItemStack> drops = event.getDrops();
        final ListIterator<ItemStack> litr = drops.listIterator();
        while (litr.hasNext()) {
            final ItemStack stack = litr.next();
            if (Journal.isJournal(onlineProfile, stack)) {
                litr.remove();
            }
            // remove all quest items and add them to backpack
            if (Utils.isQuestItem(stack)) {
                dataStorage.get(onlineProfile).addItem(stack.clone(), stack.getAmount());
                litr.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if (Boolean.parseBoolean(Config.getConfigString("remove_items_after_respawn"))) {
            // some plugins block item dropping after death and add those
            // items after respawning, so the player doesn't loose his
            // inventory after death; this aims to force removing quest
            // items, as they have been added to the backpack already
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }
            final Inventory inv = event.getPlayer().getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                if (Utils.isQuestItem(inv.getItem(i))) {
                    inv.setItem(i, null);
                }
            }
        }
        if (Boolean.parseBoolean(Config.getConfigString("journal.give_on_respawn"))) {
            dataStorage.get(PlayerConverter.getID(event.getPlayer())).getJournal().addToInv();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrameClick(final PlayerInteractEntityEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents the journal from being placed inside of item frame
        if (event.getRightClicked() instanceof ItemFrame) {
            final ItemStack item = (event.getHand() == EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand()
                    : event.getPlayer().getInventory().getItemInOffHand();

            final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
            if (Journal.isJournal(onlineProfile, item) || Utils.isQuestItem(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents players from placing "quest item" blocks
        if (Utils.isQuestItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(final PlayerItemBreakEvent event) {
        if (!Boolean.parseBoolean(Config.getConfigString("quest_items_unbreakable"))) {
            return;
        }
        // prevent quest items from breaking
        if (Utils.isQuestItem(event.getBrokenItem())) {
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
            if (Utils.isQuestItem(item) || Journal.isJournal(PlayerConverter.getID(event.getPlayer()), item)) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        } else if (!EnchantmentTarget.TOOL.includes(item.getType()) && Utils.isQuestItem(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFillEvent(final PlayerBucketFillEvent event) {
        onBucketEvent(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmptyEvent(final PlayerBucketEmptyEvent event) {
        onBucketEvent(event);
    }

    public void onBucketEvent(final PlayerBucketEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack itemMain = event.getPlayer().getInventory().getItemInMainHand();
        final ItemStack itemOff = event.getPlayer().getInventory().getItemInOffHand();
        if (Utils.isQuestItem(itemMain) || Utils.isQuestItem(itemOff)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (isJournalSlotLocked() && (Journal.isJournal(onlineProfile, event.getMainHandItem())
                || Journal.isJournal(onlineProfile, event.getOffHandItem()))) {
            event.setCancelled(true);
        }
    }

    private boolean isJournalSlotLocked() {
        return Boolean.parseBoolean(Config.getConfigString("journal.lock_default_journal_slot"));
    }
}
