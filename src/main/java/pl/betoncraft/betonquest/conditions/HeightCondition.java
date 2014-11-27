package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Checks Y height player is at (must be below)
 * @author BYK
 */
public class HeightCondition extends Condition {

	private double height;
	
	public HeightCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts=instructions.split(" ");
		for (String part : parts) {
			if(part.contains("height:")){
				height = Double.parseDouble(part.substring(7));
			}
		}
	}

	@Override
	public boolean isMet() {
		if(PlayerConverter.getPlayer(playerID).getLocation().getY() <= height){
			return true;
		}
		return false;
	}

}
