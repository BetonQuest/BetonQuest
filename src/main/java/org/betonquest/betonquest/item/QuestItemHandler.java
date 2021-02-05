package org.betonquest.betonquest.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;

/**
 * Handler for Journals.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.CommentRequired"})
public class QuestItemHandler implements Listener {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Registers the quest item handler as Listener.
     */
    public QuestItemHandler() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        final ItemStack item = event.getItemDrop().getItemStack();
        if (Journal.isJournal(playerID, item)) {
            event.getItemDrop().remove();
        } else if (Utils.isQuestItem(item)) {
            BetonQuest.getInstance().getPlayerData(playerID).addItem(item.clone(), item.getAmount());
            event.getItemDrop().remove();
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onItemMove(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        final ItemStack item;
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                item = event.getCurrentItem();
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR:
                if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    item = null;
                } else {
                    item = event.getCursor();
                }
                break;
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    item = null;
                } else {
                    item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                }
                break;
            default:
                item = null;
                break;
        }
        if (item != null && (Journal.isJournal(playerID, item) || Utils.isQuestItem(item))) {
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
        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        if (Journal.isJournal(playerID, event.getOldCursor()) || Utils.isQuestItem(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandEquip(final PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getPlayerItem();
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (item != null && (Journal.isJournal(playerID, item) || Utils.isQuestItem(item))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getEntity());
        // check if there is data for this player; NPCs don't have data
        if (BetonQuest.getInstance().getPlayerData(playerID) == null) {
            return;
        }
        // this prevents the journal from dropping on death by removing it from
        // the list of drops
        final List<ItemStack> drops = event.getDrops();
        final ListIterator<ItemStack> litr = drops.listIterator();
        while (litr.hasNext()) {
            final ItemStack stack = litr.next();
            if (Journal.isJournal(playerID, stack)) {
                litr.remove();
            }
            // remove all quest items and add them to backpack
            if (Utils.isQuestItem(stack)) {
                BetonQuest.getInstance().getPlayerData(playerID).addItem(stack.clone(), stack.getAmount());
                litr.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if ("false".equals(Config.getString("config.remove_items_after_respawn"))) {
            return;
        }
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

    @EventHandler(ignoreCancelled = true)
    public void onItemFrameClick(final PlayerInteractEntityEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents the journal from being placed inside of item frame
        if (event.getRightClicked() instanceof ItemFrame) {
            final ItemStack item = (event.getHand() == EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand()
                    : event.getPlayer().getInventory().getItemInOffHand();

            final String playerID = PlayerConverter.getID(event.getPlayer());
            if (Journal.isJournal(playerID, item) || Utils.isQuestItem(item)) {
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
        if ("false".equalsIgnoreCase(BetonQuest.getInstance().getConfig().getString("quest_items_unbreakable"))) {
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

    @EventHandler(ignoreCancelled = true)
    public void onInteractEvent(final PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getItem();
        if (event.getClickedBlock() != null) {
            final String playerID = PlayerConverter.getID(event.getPlayer());
            if (Journal.isJournal(playerID, item) || Utils.isQuestItem(item)) {
                event.setCancelled(true);
            }
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
