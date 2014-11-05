/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * 
 * @author Co0sh
 */
public class KillEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public KillEvent(String playerID, String instructions) {
		super(playerID, instructions);
		Bukkit.getPlayer(playerID).damage(Bukkit.getPlayer(playerID).getHealth() + 100);
	}

}
