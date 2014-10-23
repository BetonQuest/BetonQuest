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
public class PermissionCondition extends Condition {
	
	private String permission;
	private boolean inverted = false;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public PermissionCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.equalsIgnoreCase("--inverted")) {
				inverted = true;
			}
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
