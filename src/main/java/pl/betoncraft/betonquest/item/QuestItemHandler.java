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
package pl.betoncraft.betonquest.item;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Handler for Journals.
 *
 * @author Co0sh
 */
public class QuestItemHandler implements Listener {

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
        if (playerID == null) {
            return;
        }
        final ItemStack item = event.getItemDrop().getItemStack();
        if (item == null) {
            return;
        }
        try {
            // if journal is dropped, remove it so noone else can pick it up
            if (Journal.isJournal(playerID, item)) {
                event.getItemDrop().remove();
            } else if (Utils.isQuestItem(item)) {
                BetonQuest.getInstance().getPlayerData(playerID).addItem(item.clone(), item.getAmount());
                event.getItemDrop().remove();
            }
        } catch (final Exception e) {
            // if there is any problem with checking the item, prevent dropping
            // it
            // it will be frustrating for user but at least they won't duplicate
            // items
            event.setCancelled(true);
            LogUtils.getLogger().log(Level.WARNING, "Could not excecute onItemDrop in QuestItemHandler");
            LogUtils.logThrowable(e);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        ItemStack item;
        switch (event.getAction()) {
        case MOVE_TO_OTHER_INVENTORY:
            item = event.getCurrentItem();
            break;
        case PLACE_ALL:
        case PLACE_ONE:
        case PLACE_SOME:
        case SWAP_WITH_CURSOR:
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                item = event.getCursor();
            } else {
                item = null;
            }
            break;
        case HOTBAR_MOVE_AND_READD:
        case HOTBAR_SWAP:
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            } else {
                item = null;
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
        if (BetonQuest.getInstance().getPlayerData(playerID) == null)
            return;
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
        if (Config.getString("config.remove_items_after_respawn").equals("false"))
            return;
        // some plugins block item dropping after death and add those
        // items after respawning, so the player doesn't loose his
        // inventory after death; this aims to force removing quest
        // items, as they have been added to the backpack already
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
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
            ItemStack item = null;
            item = (event.getHand() == EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand()
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
        if (BetonQuest.getInstance().getConfig().getString("quest_items_unbreakable").equalsIgnoreCase("false")) {
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

}
