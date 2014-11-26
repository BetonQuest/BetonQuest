package pl.betoncraft.betonquest.objectives;

import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

public class DelayObjective extends Objective implements Listener {
	
	private long stamp;
	private BukkitTask runnable;

	public DelayObjective(String playerID, String instructions) {
		super(playerID, instructions);
		// if you don't define stamp: or delay: then objective will be completed immediately after creating, just like folder event
		long stamp = -1;
		for (String part : instructions.split(" ")) {
			if (part.contains("delay:")) {
				stamp = new Date().getTime() + (Integer.parseInt(part.substring(6)) * 1000 * 60);
			} else if (part.contains("stamp:")) {
				stamp = Long.parseLong(part.substring(6));
			}
		}
		final long finalStamp = stamp;
		final Listener listener = this;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (new Date().getTime() >= finalStamp && checkConditions()) {
					HandlerList.unregisterAll(listener);
					this.cancel();
					completeObjective();
				}
			}
		}.runTaskTimer(BetonQuest.getInstance(), 0, 20*60);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		HandlerList.unregisterAll(this);
		runnable.cancel();
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "delay stamp:" + stamp + " " + events + " " + conditions + " tag:" + tag;
	}
}
