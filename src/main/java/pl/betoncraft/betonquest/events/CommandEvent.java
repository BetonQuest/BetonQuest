/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

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
		String command = instructions.substring(instructions.indexOf(" ") + 1);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", PlayerConverter.getPlayer(playerID).getName()));
	}

}
