package pl.betoncraft.betonquest.objectives;

import net.citizensnpcs.api.event.NPCDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

public class NPCKillObjective extends Objective implements Listener {
	
	private int ID;
	private int amount = 1;

	public NPCKillObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		ID = Integer.parseInt(parts[1]);
		for (String part : parts) {
			if (part.contains("amount:")) {
				amount = Integer.parseInt(part.substring(7));
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onNPCKilling(NPCDeathEvent event) {
		if (event.getNPC().getId() == ID && event.getNPC().getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) event.getNPC().getEntity().getLastDamageCause();
			if (damage.getDamager() instanceof Player) {
				Player player = (Player) damage.getDamager();
				if (player.equals(PlayerConverter.getPlayer(playerID)) && checkConditions()) {
					amount--;
				}
			}
		}
		if (amount <= 0) {
			completeObjective();
			HandlerList.unregisterAll(this);
		}
	}

	@Override
	public String getInstructions() {
		return "npckill " + ID + " amount:" + amount + " " + events + " " + conditions + " tag:" + tag;
	}

}
