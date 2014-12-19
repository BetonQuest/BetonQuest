/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.vault;

import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class MoneyEvent extends QuestEvent {

	/**
	 * @param playerID
	 * @param instructions
	 */
	@SuppressWarnings("deprecation")
	public MoneyEvent(String playerID, String instructions) {
		super(playerID, instructions);
		double amount = Double.parseDouble(instructions.split(" ")[1]);
		Player player = PlayerConverter.getPlayer(playerID);
		if (amount > 0) {
			Compatibility.getEconomy().depositPlayer(player.getName(), amount);
		} else if (amount < 0) {
			amount = -amount;
			Compatibility.getEconomy().withdrawPlayer(player.getName(), amount);
		}
	}

}
