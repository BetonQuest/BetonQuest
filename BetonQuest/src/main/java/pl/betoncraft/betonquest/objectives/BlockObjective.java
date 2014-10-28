/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * Block place/break objective
 * @author Co0sh
 */
public class BlockObjective extends Objective implements Listener{
	
	private Material material;
//	private int data;
	private int neededAmount;
	private int currentAmount = 0;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public BlockObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
//		String blockType = parts[1];
//		if (blockType.contains(":")) {
//			material = Material.valueOf(blockType.split(":")[0]);
//			data = Integer.valueOf(blockType.split(":")[1]);
//		} else {
			material = Material.valueOf(parts[1]);
//			data = 0;
//		}
		neededAmount = Integer.valueOf(parts[2]);
		for (String part : parts) {
			if (part.contains("conditions:")) {
				conditions = part;
			}
			if (part.contains("events:")) {
				events = part;
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().equals(Bukkit.getPlayer(playerID)) && event.getBlock().getType().equals(material) && checkConditions()) {
			currentAmount++;
			if (currentAmount == neededAmount) {
				completeObjective();
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().equals(Bukkit.getPlayer(playerID)) && event.getBlock().getType().equals(material) && checkConditions()) {
			currentAmount--;
			if (currentAmount == neededAmount) {
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		String instruction = new String("block " + material.toString() + " " + String.valueOf(neededAmount - currentAmount) + " " + conditions + " " + events + " tag:" + tag);
		HandlerList.unregisterAll(this);
		return instruction;
	}

}
