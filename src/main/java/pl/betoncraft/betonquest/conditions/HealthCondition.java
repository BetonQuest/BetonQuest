package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.core.Condition;

public class HealthCondition extends Condition{

	private boolean inverted=false;
private String Health=null;

	public HealthCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts=instructions.split(" ");
		for (String part : parts) {
			if(part.contains("health:"))	
			{
				Health=part.substring(7);
			}
			
		}
	}

	@Override
	public boolean isMet() {
		if(Bukkit.getPlayer(super.playerID).getHealth()>=Double.parseDouble(Health))
		{
			return !inverted;
		}
		return inverted;
	}

}
