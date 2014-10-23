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
public class QuestCommand implements CommandExecutor {
	
	private String lang = ConfigInput.getString("config.language");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias,
			String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("q")) {
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
					List<String> texts = BetonQuest.getInstance().getJournal(sender.getName()).getText();
					sender.sendMessage("Dziennik");
					for (String text : texts) {
						sender.sendMessage("");
						for (String line : text.split("\\n")) {
							sender.sendMessage(line);
						}
					}
				}
			default:
				sender.sendMessage(ConfigInput.getString("messages." + lang + ".unknown_argument"));
				break;
			}
			return true;
		}
		return false;
	}

}
