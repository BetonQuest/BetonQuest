package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

public class HealthCondition extends Condition{

	private Double health = null;

	public HealthCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if(part.contains("health:")) {
				health = Double.parseDouble(part.substring(7));
			}
			
		}
	}

	@Override
	public boolean isMet() {
		if(PlayerConverter.getPlayer(playerID).getHealth() >= health)
		{
			return true;
		}
		return false;
	}

}
