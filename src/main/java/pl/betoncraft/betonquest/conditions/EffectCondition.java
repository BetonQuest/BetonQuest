/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.core.Condition;

/**
 * instructions: effect:POISON
 * @author Co0sh
 */
public class EffectCondition extends Condition {
	
	private PotionEffectType type;
	private boolean inverted = false;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public EffectCondition(String playerID, String instructions) {
		super(playerID, instructions);
		for (String part : instructions.split(" ")) {
			if (part.contains("type:")) {
				type = PotionEffectType.getByName(part.substring(5).toUpperCase());
			} else if (part.equals("--inverted")) {
				inverted = true;
			}
		}
	}

	@Override
	public boolean isMet() {
		if (Bukkit.getPlayer(playerID).hasPotionEffect(type)) {
			return !inverted;
		}
		return inverted;
	}

}
