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
package pl.betoncraft.betonquest.objectives;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to manually brew a potion.
 * 
 * @author Jakub Sapalski
 */
public class PotionObjective extends Objective implements Listener {

	private final HashMap<PotionEffectType, Integer> effects = new HashMap<>();
	private final QuestItem potion;
	private final int amount;
	private final boolean notify;
	private final HashMap<Location, String> locations = new HashMap<>();

	public PotionObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = PotionData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		potion = QuestItem.newQuestItem(packName, parts[1]);
		try {
			amount = Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		boolean tempNotify = false;
		for (String part : parts) {
			if (part.startsWith("effects:")) {
				String[] effectsArray = part.substring(8).split(",");
				for (String effect : effectsArray) {
					String[] eParts = effect.split(":");
					if (eParts.length != 2) {
						throw new InstructionParseException("Could not parse effects");
					}
					PotionEffectType type = PotionEffectType.getByName(eParts[0].toUpperCase());
					if (type == null) {
						throw new InstructionParseException("Potion type '" + eParts[0] + "' does not exist");
					}
					int duration;
					try {
						duration = Integer.parseInt(eParts[1]) * 20;
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse duration of an effect");
					}
					effects.put(type, duration);
				}
			} else if (part.equalsIgnoreCase("notify"))
				tempNotify = true;
		}
		notify = tempNotify;
	}

	@EventHandler
	public void onIngredientPut(InventoryClickEvent event) {
		if (event.getInventory().getType() != InventoryType.BREWING)
			return;
		if (event.getRawSlot() == 3 || event.getClick().equals(ClickType.SHIFT_LEFT)) {
			String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
			if (!containsPlayer(playerID))
				return;
			locations.put(((BrewingStand) event.getInventory().getHolder()).getLocation(), playerID);
		}
	}

	@EventHandler
	public void onBrew(final BrewEvent event) {
		final String playerID = locations.remove(event.getBlock().getLocation());
		if (playerID == null)
			return;
		final PotionData data = ((PotionData) dataMap.get(playerID));
		// this tracks how many potions there are in the stand before brewing
		int alreadyExistingTemp = 0;
		for (int i = 0; i < 3; i++)
			if (checkPotion(event.getContents().getItem(i)))
				alreadyExistingTemp++;
		// making it final for the runnable
		final int alreadyExisting = alreadyExistingTemp;
		new BukkitRunnable() {
			@Override
			public void run() {
				// unfinaling it for modifications
				boolean brewed = false;
				int alreadyExistingFinal = alreadyExisting;
				for (int i = 0; i < 3; i++) {
					// if there were any potions before, don't count them to
					// prevent cheating
					if (checkPotion(event.getContents().getItem(i))) {
						if (alreadyExistingFinal <= 0 && checkConditions(playerID)) {
							data.brew();
						}
						alreadyExistingFinal--;
						brewed = true;
					}
				}
				// check if the objective has been completed
				if (data.getAmount() >= amount) {
					completeObjective(playerID);
				} else if (brewed && notify) {
					Config.sendMessage(playerID, "potions_to_brew",
							new String[] { String.valueOf(amount - data.getAmount()) });
				}
			}
		}.runTask(BetonQuest.getInstance());
	}

	/**
	 * Checks if this ItemStack matches a potion defined in "effects" HashMap.
	 */
	private boolean checkPotion(ItemStack item) {
		if (item == null)
			return false;
		if (!potion.equalsI(item))
			return false;
		if (item.getItemMeta() instanceof PotionMeta) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			// count how many effects on the potion match the required effects
			int matchingEffects = 0;
			for (PotionEffect effect : meta.getCustomEffects()) {
				if (effects.keySet().contains(effect.getType()) && effects.get(effect.getType()) <= effect.getDuration()) {
					matchingEffects++;
				}
			}
			// if the amount of matching effects is equal to amount of required
			// effects, the potion is considered matching
			if (matchingEffects == effects.size()) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(amount - ((PotionData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(((PotionData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@Override
	public void stop() {
		locations.clear();
		HandlerList.unregisterAll(this);
	}

	@Override
	public String getDefaultDataInstruction() {
		return "0";
	}

	public static class PotionData extends ObjectiveData {

		private int amount;

		public PotionData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		public void brew() {
			amount++;
			update();
		}

		public int getAmount() {
			return amount;
		}

		@Override
		public String toString() {
			return String.valueOf(amount);
		}

	}

}
