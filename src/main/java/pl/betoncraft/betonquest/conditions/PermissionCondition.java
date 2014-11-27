/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class PermissionCondition extends Condition {
	
	private String permission;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public PermissionCondition(String playerID, String instructions) {
		super(playerID, instructions);
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
		if (PlayerConverter.getPlayer(playerID).hasPermission(permission)) {
			return true;
		}
		return false;
	}

}
