package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Checks if the time is right
 * @author BYK
 */
public class TimeCondition extends Condition  {

	private double timeMin = 0;
	private double timeMax = 0;
	
	public TimeCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String theTime = null;
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("time:")){
				theTime=part.substring(5);
			}
		}
		parts = theTime.split("-");
		timeMin = Double.parseDouble(parts[0]);
		timeMax = Double.parseDouble(parts[1]);
	}

	@Override
	public boolean isMet() {
		
		double time = PlayerConverter.getPlayer(playerID).getWorld().getTime();
		if(time >= 18000 && time < 24000){
			time = (time / 1000) - 18;
		} else {
			time=(time/1000)-6;
		}
		if (time > timeMin && time < timeMax) {
			return true;
		}
	
		return false;
	}

}
