/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.mythicmobs;

import net.elseland.xikage.MythicMobs.API.Events.MythicMobDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class MythicMobKillObjective extends Objective implements Listener {
	
	private String name;
	private int amount = 1;

	/**
	 * @param playerID
	 * @param instructions
	 */
	public MythicMobKillObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		name = parts[1];
		for (String part : parts) {
			if (part.contains("amount:")) {
				amount = Integer.parseInt(part.substring(7));
				break;
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onBossKill(MythicMobDeathEvent event) {
		if (event.getMobType().getInternalName().equals(name) && event.getKiller() instanceof Player) {
			Player player = (Player) event.getKiller();
			if (player.equals(PlayerConverter.getPlayer(playerID)) && checkConditions()) {
				amount--;
			}
		}
		if (amount <= 0) {
			completeObjective();
			HandlerList.unregisterAll(this);
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "mmobkill " + name + " amount:" + amount + " " + events + " " + conditions + " tag:" + tag;
	}

}
