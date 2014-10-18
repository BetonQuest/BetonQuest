/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.core.Conversation;

/**
 * 
 * @author Co0sh
 */
public class TemporatyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("conv")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			new Conversation(sender.getName(), "testconversation", new NPCLocation(((Player) sender).getLocation()));
		}
		return false;
	}

}
