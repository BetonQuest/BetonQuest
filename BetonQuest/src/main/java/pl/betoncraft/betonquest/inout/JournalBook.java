/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public class JournalBook implements Listener {

	public JournalBook() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onJournalDrop(PlayerDropItemEvent event) {
		if (isJournal(event.getItemDrop().getItemStack())) {
			event.getItemDrop().remove();
		}
	}
	
	@EventHandler
	public void onJournalMove(InventoryClickEvent event) {
		if (isJournal(event.getCursor()) && (event.getAction().equals(InventoryAction.PLACE_ALL) || event.getAction().equals(InventoryAction.PLACE_ONE) || event.getAction().equals(InventoryAction.PLACE_SOME))) {
			event.setCancelled(event.getRawSlot() < (event.getView().countSlots() - 36));
			return;
		} else if (isJournal(event.getCurrentItem()) && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onJournalDrag(InventoryDragEvent event) {
		if (isJournal(event.getOldCursor())) {
			for (Integer slot : event.getRawSlots()) {
				if (slot < (event.getView().countSlots() - 36)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		List<ItemStack> drops = event.getDrops();
		ListIterator<ItemStack> litr = drops.listIterator();
		while(litr.hasNext()) {
			ItemStack stack = litr.next();
			if(isJournal(stack)) {
				litr.remove();
				return;
			}
		}
	}
	
	public static void addJournal(String playerID, int slot) {
		if (hasJournal(playerID)) {
			return;
		}
		Inventory inventory = Bukkit.getPlayer(playerID).getInventory();
		if (slot < 0) {
			slot = 8;
		}
		ItemStack item = generateJournal(playerID);
		if (inventory.firstEmpty() >= 0) {
			ItemStack oldItem = inventory.getItem(slot);
			inventory.setItem(slot, item);
			if (oldItem != null) {
				inventory.addItem(oldItem);
			}
		} else {
			SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".inventory_full"));
		}
	}
	
	public static void updateJournal(String playerID) {
		if (hasJournal(playerID)) {
			int slot = removeJournal(playerID);
			addJournal(playerID, slot);
		}
		
	}
	
	public static boolean hasJournal(String playerID) {
		for (ItemStack item : Bukkit.getPlayer(playerID).getInventory().getContents()) {
			if (isJournal(item)) {
				return true;
			}
		}
		return false;
	}
	
	public static int removeJournal(String playerID) {
		Inventory inventory = Bukkit.getPlayer(playerID).getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			if (isJournal(inventory.getItem(i))) {
				inventory.setItem(i, new ItemStack(Material.AIR));
				return i;
			}
		}
		return -1;
	}
	
	private static boolean isJournal(ItemStack item) {
		if (item == null) {
			return false;
		}
		return (item.getType().equals(Material.WRITTEN_BOOK)
		&& ((BookMeta)item.getItemMeta()).hasTitle()
		&& ((BookMeta)item.getItemMeta()).getTitle().equals(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_title")) 
		&& item.getItemMeta().hasLore()
		&& item.getItemMeta().getLore().contains(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_lore")));
	}

	private static ItemStack generateJournal(String playerID) {
		ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) item.getItemMeta();
		meta.setTitle(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_title"));
		meta.setAuthor(playerID);
		List<String> lore = new ArrayList<String>();
		lore.add(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_lore"));
		meta.setLore(lore);
		meta.setPages(BetonQuest.getInstance().getJournal(playerID).getText());
		item.setItemMeta(meta);
		return item;
	}
}
