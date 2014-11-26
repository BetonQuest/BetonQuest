/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * @author Co0sh
 */
public class JournalCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("j")) {
			if (sender instanceof Player) {
				JournalBook.addJournal(sender.getName(), Integer.parseInt(ConfigInput.getString("config.default_journal_slot")));
			}
			return true;
		}
		return false;
	}

}
