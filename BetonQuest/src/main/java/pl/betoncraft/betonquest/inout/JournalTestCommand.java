/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public class JournalTestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("journal")) {
			if (sender instanceof Player) {
				List<String> texts = BetonQuest.getInstance().getJournal(sender.getName()).getText();
				sender.sendMessage("Dziennik");
				for (String text : texts) {
					sender.sendMessage("");
					for (String line : text.split("\\n")) {
						sender.sendMessage(line);
					}
				}
			}
			return true;
		}
		return false;
	}

}
