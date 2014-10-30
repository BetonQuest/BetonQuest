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
			if (args.length < 1) {
				// TODO wyœwietlenie pomocy
				sender.sendMessage("TODO wyswietlenie pomocy");
				return true;
			}
			switch (args[0]) {
			case "reload":
				if (!sender.hasPermission("betonquest.reload")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				ConfigInput.reload();
				sender.sendMessage(getMessage("reloaded"));
				break;
			case "journal":
				if (sender instanceof Player) {
					JournalBook.addJournal(sender.getName(), -1);
				}
				break;
			case "objectives":
				if (!sender.hasPermission("betonquest.objectives")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				if (!(sender instanceof Player)) {
					return true;
				}
				sender.sendMessage(getMessage("player_objectives"));
				for (ObjectiveSaving objective : BetonQuest.getInstance().getObjectives(sender.getName())) {
					sender.sendMessage("§b- " + objective.getTag());
				}
				break;
			case "tags":
				if (!sender.hasPermission("betonquest.tags")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				if (!(sender instanceof Player)) {
					return true;
				}
				sender.sendMessage(getMessage("player_tags"));
				List<String> tags = BetonQuest.getInstance().getPlayerStrings().get(sender.getName());
				if (tags == null) {
					return true;
				}
				for (String tag : tags) {
					sender.sendMessage("§b- " + tag);
				}
				break;
			case "condition":
				if (!sender.hasPermission("betonquest.conditions")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_condition"));
					return true;
				}
				sender.sendMessage(getMessage("player_condition").replaceAll("%condition%", ConfigInput.getString("conditions." + args[1])).replaceAll("%outcome%", BetonQuest.condition(sender.getName(), args[1]) + ""));
				break;
			case "points":
				if (!sender.hasPermission("betonquest.conditions")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_condition"));
					return true;
				}
				sender.sendMessage(getMessage("player_points").replaceAll("%category%", args[1]).replaceAll("%count%", BetonQuest.getInstance().getPlayerPoints(sender.getName(), args[1]) + ""));
				break;
			case "purge":
				if (!sender.hasPermission("betonquest.purge")) {
					sender.sendMessage(getMessage("no_permission"));
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_player"));
					return true;
				}
				BetonQuest.getInstance().purgePlayer(args[1]);
				JournalBook.updateJournal(args[1]);
				sender.sendMessage(getMessage("purged").replaceAll("%player%", args[1]));
				break;
			default:
				sender.sendMessage(getMessage("unknown_argument"));
				break;
			}
			return true;
		}
		return false;
	}
	
	private String getMessage(String name) {
		return ConfigInput.getString("messages." + lang + "." + name).replaceAll("&", "§");
	}

}
