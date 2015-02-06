/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
	
	@EventHandler
	public void onItemFrameClick(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame && isJournal(event.getPlayer().getItemInHand())) {
			event.setCancelled(true);
		}
	}
	
	public static void addJournal(String playerID, int slot) {
		if (hasJournal(playerID)) {
			return;
		}
		Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
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
			SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".inventory_full"), ConfigInput.getString("config.sounds.full"));
		}
	}
	
	public static void updateJournal(String playerID) {
		if (hasJournal(playerID)) {
			int slot = removeJournal(playerID);
			addJournal(playerID, slot);
		}
		
	}
	
	public static boolean hasJournal(String playerID) {
		for (ItemStack item : PlayerConverter.getPlayer(playerID).getInventory().getContents()) {
			if (isJournal(item)) {
				return true;
			}
		}
		return false;
	}
	
	public static int removeJournal(String playerID) {
		Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
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
		&& ((BookMeta)item.getItemMeta()).getTitle().equals(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_title").replaceAll("&", "§")) 
		&& item.getItemMeta().hasLore()
		&& item.getItemMeta().getLore().contains(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_lore").replaceAll("&", "§")));
	}

	private static ItemStack generateJournal(String playerID) {
		ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) item.getItemMeta();
		meta.setTitle(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_title").replaceAll("&", "§"));
		meta.setAuthor(PlayerConverter.getPlayer(playerID).getName());
		List<String> lore = new ArrayList<String>();
		lore.add(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".journal_lore").replaceAll("&", "§"));
		meta.setLore(lore);
		
		// logic for converting entries into single text and then to pages
		StringBuilder stringBuilder = new StringBuilder();
		for (String entry : BetonQuest.getInstance().getJournal(playerID).getText()) {
			stringBuilder.append(entry.replaceAll("&", "§") + "\n§" + ConfigInput.getString("config.journal_colors.line") + "---------------\n");
		}
		String wholeString = stringBuilder.toString().trim();
		// end of this logic
		
		meta.setPages(pagesFromString(wholeString, true));
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Converts string to list of pages for a book. SingleString defines
	 * if you passed a string separated by "|" for every page. False means
	 * that it is separated, true that it isn't.
	 * @param string
	 * @param singleString
	 * @return
	 */
	public static List<String> pagesFromString(String string, boolean singleString) {
		List<String> pages = new ArrayList<>();
		if (singleString) {
			StringBuilder page = new StringBuilder();
			for (String word : string.split(" ")) {
				if (page.length() + word.length() + 1 > 245) {
					pages.add(page.toString().trim());
					page = new StringBuilder();
				}
				page.append(word + " ");
			}
			pages.add(page.toString().trim());
		} else {
			pages = Arrays.asList(string.replaceAll("\\\\n", "\n").split("\\|"));
		}
		return pages;
	}
}
