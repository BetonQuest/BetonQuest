/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * instructions: effect:POISON
 * @author Co0sh
 */
public class EffectCondition extends Condition {
	
	private PotionEffectType type;

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
			}
		}
	}

	@Override
	public boolean isMet() {
		if (PlayerConverter.getPlayer(playerID).hasPotionEffect(type)) {
			return true;
		}
		return false;
	}

}
