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
public class QuestCommand implements CommandExecutor {
	
	private String lang = ConfigInput.getString("config.language");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias,
			String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("q")) {
			if (args.length < 1) {
				// TODO wyœwietlenie pomocy
				sender.sendMessage("TODO wyswietlenie pomocy");
				return true;
			}
			switch (args[0]) {
			case "reload":
				if (!sender.hasPermission("betonquest.reload")) {
					sender.sendMessage(ConfigInput.getString("messages." + lang + ".no_permission").replace("&", "§"));
					return true;
				}
				ConfigInput.reload();
				sender.sendMessage(ConfigInput.getString("messages." + lang + ".reloaded").replace("&", "§"));
				break;
			case "journal":
				if (sender instanceof Player) {
					JournalBook.addJournal(sender.getName(), -1);
				}
				break;
			default:
				sender.sendMessage(ConfigInput.getString("messages." + lang + ".unknown_argument").replace("&", "§"));
				break;
			}
			return true;
		}
		return false;
	}

}
