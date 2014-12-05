/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.ConfigAccessor;

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
				sender.sendMessage("§c/q reload §b- " + getMessage("command_reload"));
				sender.sendMessage("§c/q objectives §b- " + getMessage("command_objectives"));
				sender.sendMessage("§c/q tags §b- " + getMessage("command_tags"));
				sender.sendMessage("§c/q points §b- " + getMessage("command_tags"));
				sender.sendMessage("§c/q condition <id> §b- " + getMessage("command_condition"));
				sender.sendMessage("§c/q event <id> §b- " + getMessage("command_event"));
				sender.sendMessage("§c/q item <id> §b- " + getMessage("command_event"));
				sender.sendMessage("§c/q purge <player> §b- " + getMessage("command_purge"));
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
			case "item":
				if (!(sender instanceof Player)) {
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(getMessage("specify_item"));
					return true;
				}
				Player player = (Player) sender;
				ItemStack item = player.getItemInHand();
				if (item == null) {
					sender.sendMessage(getMessage("no_item"));
					return true;
				}
				ConfigAccessor config = ConfigInput.getConfigs().get("items");
				String name = "";
				String lore = "";
				String enchants = "";
				String title = "";
				String text = "";
				String author = "";
				String effects = "";
				
				// TODO item creation
				ItemMeta meta = item.getItemMeta();
				if (meta.hasDisplayName()) {
					name = " name:" +meta.getDisplayName().replace(" ", "_");
				}
				if (meta.hasLore()) {
					StringBuilder string = new StringBuilder();
					for (String line : meta.getLore()) {
						string.append(line + ";");
					}
					lore = " lore:" + string.substring(0, string.length()-1).replace(" ", "_");
				}
				if (meta.hasEnchants()) {
					StringBuilder string = new StringBuilder();
					for (Enchantment enchant : meta.getEnchants().keySet()) {
						string.append(enchant.getName() + ":" + meta.getEnchants().get(enchant) + ",");
					}
					enchants = " enchants:" + string.substring(0, string.length()-1);
				}
				if (meta instanceof BookMeta) {
					BookMeta bookMeta = (BookMeta) meta;
					if (bookMeta.hasAuthor()) {
						author = " author:" + bookMeta.getAuthor().replace(" ", "_");
					}
					if (bookMeta.hasTitle()) {
						title = " title:" + bookMeta.getTitle().replace(" ", "_");
					}
					if (bookMeta.hasPages()) {
						StringBuilder string = new StringBuilder();
						BetonQuest.getInstance().getLogger().info(bookMeta.getPages().toString());
						for (String page : bookMeta.getPages()) {
							string.append(page.trim().replace(" ", "_") + "_");
						}
						text = " text:" + string.substring(0, string.length()-1);
					}
				}
				if (meta instanceof PotionMeta) {
					PotionMeta potionMeta = (PotionMeta) meta;
					if (potionMeta.hasCustomEffects()) {
						StringBuilder string = new StringBuilder();
						for (PotionEffect effect : potionMeta.getCustomEffects()) {
							int power = effect.getAmplifier() + 1;
							int duration = (effect.getDuration() - (effect.getDuration() % 20)) / 20;
							string.append(effect.getType().getName() + ":" + power + ":" + duration + ",");
						}
						effects = " effects:" + string.substring(0, string.length()-1);
					}
				}
				
				@SuppressWarnings("deprecation")
				String instructions = item.getType() + " data:" + item.getData().getData() + name + lore + enchants + title + author + text + effects;
				config.getConfig().set(args[1], instructions.trim());
				config.saveConfig();
				sender.sendMessage(getMessage("item_created").replace("%item%", args[1]));
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
