package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.Condition;

public class ExperienceCondition extends Condition{
	
	private boolean inverted = false;
	private int experience;

	public ExperienceCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.equalsIgnoreCase("--inverted")) {
				inverted = true;
			} else if (part.contains("exp:")) {
				experience = Integer.valueOf(part.substring(4));
			}
		}
	}

	@Override
	public boolean isMet() {
		if (Bukkit.getPlayer(playerID).getLevel() > experience) {
			return !inverted;
		}
		return inverted;
	}

}
