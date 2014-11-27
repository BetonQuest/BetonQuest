package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

public class ExperienceCondition extends Condition{
	
	private int experience;

	public ExperienceCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("exp:")) {
				experience = Integer.valueOf(part.substring(4));
			}
		}
	}

	@Override
	public boolean isMet() {
		if (PlayerConverter.getPlayer(playerID).getLevel() > experience) {
			return true;
		}
		return false;
	}

}
