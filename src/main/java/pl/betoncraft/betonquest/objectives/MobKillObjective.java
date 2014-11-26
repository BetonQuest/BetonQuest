/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class MobKillObjective extends Objective implements Listener {
	
	private EntityType mobType;
	private int amount;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public MobKillObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		mobType = EntityType.valueOf(parts[1]);
		amount = Integer.valueOf(parts[2]);
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			if (damage.getDamager() instanceof Player && ((Player) damage.getDamager()).getName().equals(playerID) && damage.getEntity().getType().equals(mobType) && checkConditions()) {
				amount--;
				if (amount == 0) {
					HandlerList.unregisterAll(this);
					completeObjective();
				}
			}
		} else if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
			Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
			if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).getName().equals(playerID) && event.getEntity().getType().equals(mobType) && checkConditions()) {
				amount--;
				if (amount == 0) {
					HandlerList.unregisterAll(this);
					completeObjective();
				}
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "mobkill " + mobType.toString() + " " + amount + " " + conditions + " " + events + " tag:" + tag;
	}

}
