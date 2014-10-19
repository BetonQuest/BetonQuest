/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.Condition;

/**
 * 
 * @author Co0sh
 */
public class TestCondition extends Condition {
	
	private String permission;
	private boolean inverted;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TestCondition(String playerID, String instructions) {
		super(playerID, instructions);
		if (instructions.contains(" inverted ")) {
			inverted = true;
		} else {
			inverted = false;
		}
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("perm:")) {
				permission = part.substring(5);
				break;
			}
		}
	}

	@Override
	public boolean isMet() {
		if (Bukkit.getPlayer(super.playerID).hasPermission(permission)) {
			return !inverted;
		}
		return inverted;
	}

}
