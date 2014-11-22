/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.Conversation;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.UnifiedLocation;

/**
 * 
 * @author Co0sh
 */
public class ConversationEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ConversationEvent(String playerID, String instructions) {
		super(playerID, instructions);
		new Conversation(playerID, instructions.split(" ")[1], new UnifiedLocation(Bukkit.getPlayer(playerID).getLocation()));
	}

}
