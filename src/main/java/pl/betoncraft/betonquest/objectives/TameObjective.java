/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class TameObjective extends Objective implements Listener {
	
	public enum TamableMobs {
		WOLF,
		OCELOT,
		HORSE;
	}
	
	private TamableMobs type;
	private int amount;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TameObjective(String playerID, String instructions) {
		super(playerID, instructions);
		type = TamableMobs.valueOf(instructions.split(" ")[1]);
		amount = Integer.parseInt(instructions.split(" ")[2]);
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onTaming(EntityTameEvent event) {
		if (!((Player) event.getOwner()).equals(PlayerConverter.getPlayer(playerID))) {
			return;
		}
		LivingEntity entity = event.getEntity();
		switch (type) {
		case WOLF:
			if (entity.getType().equals(EntityType.WOLF) && checkConditions()) {
				amount--;
			}
			break;
		case OCELOT:
			if (entity.getType().equals(EntityType.OCELOT) && checkConditions()) {
				amount--;
			}
			break;
		case HORSE:
			if (entity.getType().equals(EntityType.HORSE) && checkConditions()) {
				amount--;
			}
			break;
		default:
			break;
		}
		if (amount <= 0) {
			HandlerList.unregisterAll(this);
			completeObjective();
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "tame " + type + " " + amount + " " + conditions + " " + events + " tag:" + tag;
	}

}
