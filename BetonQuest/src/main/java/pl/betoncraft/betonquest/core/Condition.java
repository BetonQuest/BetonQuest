/**
 * 
 */
package pl.betoncraft.betonquest.core;


/**
 * 
 * @author Co0sh
 */
abstract public class Condition {
	
	protected String playerID;
	protected String instructions;
	
	public Condition(String playerID, String instructions) {
		this.playerID = playerID;
		this.instructions = instructions;
	}
	
	abstract public boolean isMet();
}
