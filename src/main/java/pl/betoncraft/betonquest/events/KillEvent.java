/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

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
		Player player = PlayerConverter.getPlayer(playerID);
		player.damage(player.getHealth() + 100);
	}

}
