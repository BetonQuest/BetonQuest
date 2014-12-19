/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.vault;

import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class MoneyCondition extends Condition {

	private double amount = 0;
	
	/**
	 * @param playerID
	 * @param instructions
	 */
	public MoneyCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("amount:")) {
				amount = Double.parseDouble(part.substring(7));
				if (amount < 0) {
					amount = 0;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isMet() {
		return Compatibility.getEconomy().has(PlayerConverter.getPlayer(playerID).getName(), amount);
	}

}
