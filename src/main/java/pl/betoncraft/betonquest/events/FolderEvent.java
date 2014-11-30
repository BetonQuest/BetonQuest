package pl.betoncraft.betonquest.events;

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

public class FolderEvent extends QuestEvent {

	public FolderEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		String[] events = null;
		int delay = 0;
		for (String part : parts) {
			if (part.contains("events:")) {
				events = part.substring(7).split(",");
			}
			if (part.contains("delay:")) {
				delay = Integer.parseInt(part.substring(6));
			}
		}
		if (events == null) {
			BetonQuest.getInstance().getLogger().severe("Error in folder event: events not defined! " + instructions);
			return;
		}
		final String[] finalEvents = events;
		final String player = playerID;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (PlayerConverter.getPlayer(player) == null) {
					this.cancel();
					return;
				}
				for (String event : finalEvents) {
					BetonQuest.event(player, event);
				}
			}
		}.runTaskLater(BetonQuest.getInstance(), delay * 20);
	}

}
