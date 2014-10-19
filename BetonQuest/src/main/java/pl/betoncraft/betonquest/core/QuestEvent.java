/**
 * 
 */
package pl.betoncraft.betonquest.core;

/**
 * 
 * @author Co0sh
 */
public abstract class QuestEvent {
	
	protected String playerID;
	protected String instructions;

	public QuestEvent(String playerID, String instructions) {
		this.playerID = playerID;
		this.instructions = instructions;
	}
}
