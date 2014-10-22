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
public class TagCondition extends Condition {
	
	private String string;
	private boolean inverted = false;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TagCondition(String playerID, String instructions) {
		super(playerID, instructions);
		if (instructions.contains("--inverted")) {
			inverted = true;
		}
		string = instructions.split(" ")[instructions.split(" ").length - 1];
	}

	/* (non-Javadoc)
	 * @see pl.betoncraft.betonquest.core.Condition#isMet()
	 */
	@Override
	public boolean isMet() {
		if (BetonQuest.getInstance().havePlayerString(playerID, string)) {
			return !inverted;
		}
		return inverted;
	}

}
