/**
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
package pl.betoncraft.betonquest;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.config.Config;
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

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		String playerID = PlayerConverter.getID((Player) event.getPlayer());
		ItemStack item = event.getItemDrop().getItemStack();
		// if journal is dropped, remove it so noone else can pick it up
		if (Journal.isJournal(playerID, item)) {
			event.getItemDrop().remove();
		} else if (Utils.isQuestItem(item)) {
			BetonQuest.getInstance().getPlayerData(playerID).addItem(item.clone(), item.getAmount());
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onItemMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (event.getView().getType().equals(InventoryType.CREATIVE)) {
			return;
		}
		if (event.getView().getType().equals(InventoryType.CRAFTING)) {
			return;
		}
		String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
		// canceling all action that could lead to transfering the journal
		if (Journal.isJournal(playerID, event.getCursor()) || Utils.isQuestItem(event.getCursor())) {
			if (event.getAction().equals(InventoryAction.PLACE_ALL)
					|| event.getAction().equals(InventoryAction.PLACE_ONE)
					|| event.getAction().equals(InventoryAction.PLACE_SOME)) {
				// this blocks normal clicking outside of the inventory
				boolean isOutside = event.getRawSlot() < (event.getView().countSlots() - 36);
				if (isOutside) {
					event.setCancelled(true);
				}
			}
		} else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
			if (Journal.isJournal(playerID, event.getCurrentItem()) || Utils.isQuestItem(event.getCurrentItem())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrag(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
		// this is moving the item across the inventory outside of Player's
		// inventory
		if (Journal.isJournal(playerID, event.getOldCursor()) || Utils.isQuestItem(event.getOldCursor())) {
			for (Integer slot : event.getRawSlots()) {
				if (slot < (event.getView().countSlots() - 36)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		String playerID = PlayerConverter.getID((Player) event.getEntity());
		// check if there is data for this player; NPCs don't have data
		if (BetonQuest.getInstance().getPlayerData(playerID) == null)
			return;
		// this prevents the journal from dropping on death by removing it from
		// the list of drops
		List<ItemStack> drops = event.getDrops();
		ListIterator<ItemStack> litr = drops.listIterator();
		while (litr.hasNext()) {
			ItemStack stack = litr.next();
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		if (Config.getString("config.remove_items_after_respawn").equals("false"))
			return;
		// some plugins block item dropping after death and add those
		// items after respawning, so the player doesn't loose his
		// inventory after death; this aims to force removing quest
		// items, as they have been added to the backpack already
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		Inventory inv = event.getPlayer().getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			if (Utils.isQuestItem(inv.getItem(i))) {
				inv.setItem(i, null);
			}
		}
	}

	@EventHandler
	public void onItemFrameClick(PlayerInteractEntityEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		// this prevents the journal from being placed inside of item frame
		if (event.getRightClicked() instanceof ItemFrame) {
			ItemStack item = (event.getHand() == EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand() : event.getPlayer().getInventory().getItemInOffHand();
			String playerID = PlayerConverter.getID(event.getPlayer());
			if (Journal.isJournal(playerID, item) || Utils.isQuestItem(item)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		// this prevents players from placing "quest item" blocks
		if (Utils.isQuestItem(event.getItemInHand())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent event) {
		// prevent quest items from breaking
		if (Utils.isQuestItem(event.getBrokenItem())) {
			event.getBrokenItem().setAmount(1);
		}
	}
}
