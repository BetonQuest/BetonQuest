/**
 * 
 */
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

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
		String message = super.instructions.substring(super.instructions.split(" ")[0].length() + 1);
		PlayerConverter.getPlayer(playerID).sendMessage(message.replaceAll("&", "§").replaceAll("%player%", PlayerConverter.getPlayer(playerID).getName()));
	}

}
