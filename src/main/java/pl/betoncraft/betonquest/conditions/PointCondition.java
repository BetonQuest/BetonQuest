/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Condition;

/**
 * 
 * @author Co0sh
 */
public class PointCondition extends Condition {
	
	private String category = null;
	private int count = 0;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public PointCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("category:")) {
				category = part.substring(9);
			}
			if (part.contains("count:")) {
				count = Integer.valueOf(part.substring(6));
			}
		}
		if (category == null) {
			category = "global";
		}
	}

	@Override
	public boolean isMet() {
		if (BetonQuest.getInstance().getPlayerPoints(playerID, category) >= count) {
			return true;
		}
		return false;
	}

}
