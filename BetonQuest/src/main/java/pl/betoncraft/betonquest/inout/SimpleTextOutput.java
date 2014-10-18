/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;

/**
 * 
 * @author Co0sh
 */
public class SimpleTextOutput {
	
	/**
	 * Sends player a message that is supposed to look like a plugin notification 
	 * @param playerID
	 * @param message
	 */
	public static void sendSystemMessage(String playerID, String message) {
		String finalString = (ConfigInput.getString("messages.global.plugin_prefix") + message).replaceAll("&", "§");
		Bukkit.getServer().getPlayer(playerID).sendMessage(finalString);
	}
	
	/**
	 * Sends player a message that looks like quester said it. All "%player%" in message are replaced with player's name, all "%quester%" are replaced by quester's name
	 * @param playerID
	 * @param quester
	 * @param message
	 */
	public static void sendQuesterMessage(String playerID, String quester, String message) {
		String finalString = (ConfigInput.getString("messages.global.quester_line_format") + message).replaceAll("%player%", playerID).replaceAll("%quester%", quester).replaceAll("&", "§");
		Bukkit.getServer().getPlayer(playerID).sendMessage(finalString);
	}
	
	/**
	 * Sends player a message that looks like an option to reply to npc
	 * @param playerID
	 * @param number
	 * @param quester
	 * @param message
	 */
	public static void sendQuesterReply(String playerID, int number, String quester, String message) {
		String finalString = (ConfigInput.getString("messages.global.quester_reply_format") + message).replaceAll("%quester%", quester).replaceAll("%number%", String.valueOf(number)).replaceAll("%player%", playerID).replaceAll("&", "§");
		Bukkit.getServer().getPlayer(playerID).sendMessage(finalString);
	}
	
	public static void sendPlayerReply(String playerID, String quester, String message) {
		String finalString = (ConfigInput.getString("messages.global.player_reply_format") + message).replaceAll("%player%", playerID).replaceAll("%quester%", quester).replaceAll("&", "§");
		Bukkit.getServer().getPlayer(playerID).sendMessage(finalString);
	}
}
