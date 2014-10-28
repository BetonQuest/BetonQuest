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
public class MessageEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public MessageEvent(String playerID, String instructions) {
		super(playerID, instructions);
		if (!fire) {
			return;
		}
		String message = super.instructions.substring(super.instructions.split(" ")[0].length() + 1);
		Bukkit.getPlayer(super.playerID).sendMessage(message.replaceAll("&", "§").replaceAll("%player%", super.playerID));
	}

}
