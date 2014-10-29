package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.Condition;

/**
 * Checks Y height player is at (must be below)
 * @author BYK
 */
public class HeightCondition extends Condition {

	private boolean inverted = false;
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
		if(Bukkit.getPlayer(playerID).getLocation().getY() <= height){
			return !inverted;
		}
		return inverted;
	}

}
