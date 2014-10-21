/**
 * 
 */
package pl.betoncraft.betonquest.core;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ObjectiveSaving;


/**
 * This class represents an objective. You must extend it and register using registerObjectives() method from this plugin instance.
 * @author Co0sh
 */
public abstract class Objective {
	
	protected String playerID;
	protected String instructions;
	private ObjectiveSaving listener;
	
	public Objective(String playerID, String instructions) {
		this.playerID = playerID;
		this.instructions = instructions;
		listener = new ObjectiveSaving(playerID, this);
	}
	
/**
 * Use this method to when your objective is completed. Unregister all Listeners before!
 */
	protected void completeObjective() {
		// split instructions
		String[] parts = instructions.split(" ");
		String rawEvents = null;
		// find part with events
		for (String part : parts) {
			if (part.contains("events:")) {
				// extrapolate events
				rawEvents = part.substring(7);
				break;
			}
		}
		// if there are any events, do something with them
		if (rawEvents != null && !rawEvents.equalsIgnoreCase("")) {
			// split them to separate ids
			String[] events = rawEvents.split(",");
			// fire all events
			for (String eventID : events) {
				BetonQuest.event(playerID, eventID);
			}
		}
		listener.unregister();
	}
	
/**
 * Use this method to check if all conditions have been met before accepting objective completion.
 */
	protected boolean checkConditions() {
		// split instructions
		String[] parts = instructions.split(" ");
		String rawConditions = null;
		// find part with conditions
		for (String part : parts) {
			if (part.contains("conditions:")) {
				// extrapolate conditions
				rawConditions = part.substring(11);
				break;
			}
		}
		// if there are any conditions, do something with them
		if (rawConditions != null && !rawConditions.equalsIgnoreCase("")) {
			// split them to separate ids
			String[] conditions = rawConditions.split(",");
			// if some condition is not met, return false
			for (String conditionID : conditions) {
				if (!BetonQuest.condition(playerID, conditionID)) {
					return false;
				}
			}
		}
		// if there are no conditions or all of them are met return true
		return true;
	}
	
	/**
	 * This method has to return instructions string for current state of objective and end this object's work (eg. unregister all Listeners etc.)
	 * @return
	 */
	abstract public String getInstructions();

}