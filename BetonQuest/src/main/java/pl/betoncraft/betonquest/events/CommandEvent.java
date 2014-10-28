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
public class CommandEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public CommandEvent(String playerID, String instructions) {
		super(playerID, instructions);
		if (!fire) {
			return;
		}
		String command = instructions.substring(instructions.indexOf(" ") + 1);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", playerID));
	}

}
