/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class EffectEvent extends QuestEvent {
	
	private PotionEffectType effect;
	private int duration;
	private int amplifier;
	private boolean ambient = false;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public EffectEvent(String playerID, String instructions) {
		super(playerID, instructions);
		effect = PotionEffectType.getByName(instructions.split(" ")[1]);
		duration = Integer.parseInt(instructions.split(" ")[2]);
		amplifier = Integer.parseInt(instructions.split(" ")[3]);
		if (instructions.contains("--ambient")) {
			ambient = true;
		}
		PlayerConverter.getPlayer(playerID).addPotionEffect(new PotionEffect(effect, duration * 20, amplifier - 1, ambient));
	}

}
