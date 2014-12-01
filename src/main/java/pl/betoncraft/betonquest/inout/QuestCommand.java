/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.List;

import org.bukkit.Bukkit;
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
				sender.sendMessage("§e----- §aBetonQuest §e-----");
				sender.sendMessage("§c/q reload §b- reload the plugin");
				sender.sendMessage("§c/q objectives §b- display your objectives");
				sender.sendMessage("§c/q tags §b- display your tags");
				sender.sendMessage("§c/q points §b- display your points");
				sender.sendMessage("§c/q condition <id> §b- check if you meet condition");
				sender.sendMessage("§c/q event <id> §b- fires an event");
				sender.sendMessage("§c/q purge <player> §b- reset all player's data");
				return true;
			}
			switch (args[0]) {
			case "reload":
				ConfigInput.reload();
				Bukkit.getPluginManager().disablePlugin(BetonQuest.getInstance());
				Bukkit.getPluginManager().enablePlugin(BetonQuest.getInstance());
				sender.sendMessage(getMessage("reloaded"));
				break;
			case "objectives":
				if (!(sender instanceof Player)) {
					return true;
				}
				sender.sendMessage(getMessage("player_objectives"));
				for (ObjectiveSaving objective : BetonQuest.getInstance().getObjectives(PlayerConverter.getID((Player) sender))) {
					sender.sendMessage("§b- " + objective.getTag());
				}
				break;
			case "tags":
				if (!(sender instanceof Player)) {
					return true;
				}
				sender.sendMessage(getMessage("player_tags"));
				List<String> tags = BetonQuest.getInstance().getPlayerTags().get(PlayerConverter.getID((Player) sender));
				if (tags == null) {
					return true;
				}
				for (String tag : tags) {
					sender.sendMessage("§b- " + tag);
				}
				break;
			case "condition":
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_condition"));
					return true;
				}
				sender.sendMessage(getMessage("player_condition").replaceAll("%condition%", ConfigInput.getString("conditions." + args[1])).replaceAll("%outcome%", BetonQuest.condition(PlayerConverter.getID((Player) sender), args[1]) + ""));
				break;
			case "event":
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_event"));
					return true;
				}
				BetonQuest.event(PlayerConverter.getID((Player) sender), args[1]);
				sender.sendMessage(getMessage("player_event").replaceAll("%event%", ConfigInput.getString("events." + args[1])));
				break;
			case "points":
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_condition"));
					return true;
				}
				sender.sendMessage(getMessage("player_points").replaceAll("%category%", args[1]).replaceAll("%count%", BetonQuest.getInstance().getPlayerPoints(PlayerConverter.getID((Player) sender), args[1]) + ""));
				break;
			case "purge":
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_player"));
					return true;
				}
				BetonQuest.getInstance().purgePlayer(PlayerConverter.getID(args[1]));
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
