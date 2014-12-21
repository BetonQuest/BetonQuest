/**
 * 
 */
package pl.betoncraft.betonquest.core;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.UnifiedLocation;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocation {

	private UnifiedLocation location;
	private String[] conditions;
	private String[] events;
	private int distance;
	private String tag;

	public GlobalLocation(String event) {
		String instructions = ConfigInput.getString("events." + event);
		if (instructions == null) {
			BetonQuest.getInstance().getLogger().severe("Global location not found: " + event);
			return;
		}
		String[] parts = instructions.split(" ");
		String[] rawLocation = parts[2].split(";");
		location = new UnifiedLocation(Double.parseDouble(rawLocation[0]), Double.parseDouble(rawLocation[1]), Double.parseDouble(rawLocation[2]), rawLocation[3]);
		for (String part : parts) {
			if (part.contains("conditions:")) {
				conditions = part.substring(11).split(",");
			}
			if (part.contains("events:")) {
				events = part.substring(7).split(",");
			}
			if (part.contains("tag:")) {
				tag = part.substring(4);
			}
		}
		distance = Integer.parseInt(rawLocation[4]);
	}

	/**
	 * @return the location
	 */
	public UnifiedLocation getLocation() {
		return location;
	}

	/**
	 * @return the conditions
	 */
	public String[] getConditions() {
		return conditions;
	}

	/**
	 * @return the events
	 */
	public String[] getEvents() {
		return events;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
}
