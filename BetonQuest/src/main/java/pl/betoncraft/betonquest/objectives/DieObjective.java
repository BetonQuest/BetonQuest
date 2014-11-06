/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class DieObjective extends Objective implements Listener {
	
	private boolean cancel = false;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public DieObjective(String playerID, String instructions) {
		super(playerID, instructions);
		if (instructions.contains("cancel")) {
			cancel = true;
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (cancel) {
			return;
		}
		if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getName().equals(playerID) && checkConditions()) {
			HandlerList.unregisterAll(this);
			completeObjective();
		}
	}
	
	@EventHandler
	public void onLastDamage(EntityDamageEvent event) {
		if (!cancel) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			// TODO przetestowaæ ile zdrowia mo¿e mieæ gracz zanim umrze
			if (player.getName().equals(playerID) && player.getHealth() - event.getDamage() < 0 && checkConditions()) {
				player.setHealth(player.getMaxHealth());
				HandlerList.unregisterAll(this);
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return instructions;
	}

}
